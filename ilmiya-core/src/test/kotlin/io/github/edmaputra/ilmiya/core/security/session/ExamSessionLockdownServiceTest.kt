package io.github.edmaputra.ilmiya.core.security.session

import io.github.edmaputra.iam.application.port.out.SessionRegistryPort
import io.github.edmaputra.iam.application.port.out.TokenRevocationPort
import io.github.edmaputra.iam.domain.model.SessionId
import io.github.edmaputra.iam.domain.model.UserId
import io.github.edmaputra.iam.domain.model.UserSession
import io.github.edmaputra.iam.domain.tenancy.TenantId as IamTenantId
import io.github.edmaputra.ilmiya.common.domain.TenantId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import java.time.Instant
import java.util.UUID

class ExamSessionLockdownServiceTest {

    private lateinit var sessionRegistry: SessionRegistryPort
    private lateinit var tokenRevocationPort: TokenRevocationPort
    private lateinit var service: ExamSessionLockdownService

    private val studentUuid = UUID.randomUUID()
    private val studentIdStr = studentUuid.toString()
    private val tenantUuid = UUID.randomUUID()
    private val tenantId = TenantId.of(tenantUuid.toString())

    private val iamUserId = UserId(studentUuid)
    private val iamTenantId = IamTenantId(tenantUuid)

    @BeforeEach
    fun setUp() {
        sessionRegistry = mock(SessionRegistryPort::class.java)
        tokenRevocationPort = mock(TokenRevocationPort::class.java)
        service = ExamSessionLockdownService(sessionRegistry, tokenRevocationPort)
    }

    @Test
    fun `getActiveSessionCount should query session registry with converted identifiers`() {
        `when`(sessionRegistry.countActiveSessions(iamUserId, iamTenantId)).thenReturn(2)

        val count = service.getActiveSessionCount(studentIdStr, tenantId)

        assertThat(count).isEqualTo(2)
        verify(sessionRegistry).countActiveSessions(iamUserId, iamTenantId)
    }

    @Test
    fun `listStudentSessions should return mapped DTOs for active sessions`() {
        val now = Instant.now()
        val session1 = UserSession(
            SessionId.generate(),
            iamUserId,
            iamTenantId,
            "token-jti-1",
            now.minusSeconds(60),
            now.plusSeconds(3600),
            now,
            "192.168.1.100",
            "Mozilla/5.0 Chrome",
            false
        )
        `when`(sessionRegistry.findActiveSessions(iamUserId, iamTenantId)).thenReturn(listOf(session1))

        val results = service.listStudentSessions(studentIdStr, tenantId)

        assertThat(results).hasSize(1)
        assertThat(results[0].sessionId).isEqualTo(session1.id().value().toString())
        assertThat(results[0].tokenIdentifier).isEqualTo("token-jti-1")
        assertThat(results[0].ipAddress).isEqualTo("192.168.1.100")
        assertThat(results[0].userAgent).isEqualTo("Mozilla/5.0 Chrome")
    }

    @Test
    fun `enforceSingleSession should do nothing when 1 or fewer sessions exist`() {
        val now = Instant.now()
        val session1 = UserSession(
            SessionId.generate(),
            iamUserId,
            iamTenantId,
            "token-jti-1",
            now,
            now.plusSeconds(3600),
            now,
            "192.168.1.100",
            "Chrome",
            false
        )
        `when`(sessionRegistry.findActiveSessions(iamUserId, iamTenantId)).thenReturn(listOf(session1))

        val result = service.enforceSingleSession(studentIdStr, tenantId, "token-jti-1")

        assertThat(result.totalFound).isEqualTo(1)
        assertThat(result.terminatedCount).isEqualTo(0)
        assertThat(result.retainedTokenIdentifier).isEqualTo("token-jti-1")

        verify(sessionRegistry, never()).revokeSession(org.mockito.ArgumentMatchers.any())
        verify(tokenRevocationPort, never()).revokeToken(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any())
    }

    @Test
    fun `enforceSingleSession should revoke secondary sessions keeping specified token`() {
        val now = Instant.now()
        val sessionToKeep = UserSession(
            SessionId.generate(),
            iamUserId,
            iamTenantId,
            "exam-terminal-token",
            now.minusSeconds(100),
            now.plusSeconds(3600),
            now,
            "10.0.0.1",
            "ExamBrowser",
            false
        )
        val rogueSession = UserSession(
            SessionId.generate(),
            iamUserId,
            iamTenantId,
            "phone-token",
            now.minusSeconds(50),
            now.plusSeconds(3600),
            now,
            "192.168.1.50",
            "MobileSafari",
            false
        )
        `when`(sessionRegistry.findActiveSessions(iamUserId, iamTenantId))
            .thenReturn(listOf(sessionToKeep, rogueSession))

        val result = service.enforceSingleSession(studentIdStr, tenantId, "exam-terminal-token")

        assertThat(result.totalFound).isEqualTo(2)
        assertThat(result.terminatedCount).isEqualTo(1)
        assertThat(result.retainedTokenIdentifier).isEqualTo("exam-terminal-token")

        verify(sessionRegistry).revokeSession(rogueSession.id())
        verify(tokenRevocationPort).revokeToken("phone-token", rogueSession.expiresAt())
        verify(sessionRegistry, never()).revokeSession(sessionToKeep.id())
    }

    @Test
    fun `enforceSingleSession without keepTokenIdentifier should retain newest session and terminate older ones`() {
        val now = Instant.now()
        val olderSession = UserSession(
            SessionId.generate(),
            iamUserId,
            iamTenantId,
            "older-token",
            now.minusSeconds(300),
            now.plusSeconds(3600),
            now,
            "10.0.0.1",
            "PC1",
            false
        )
        val newestSession = UserSession(
            SessionId.generate(),
            iamUserId,
            iamTenantId,
            "newest-token",
            now.minusSeconds(10),
            now.plusSeconds(3600),
            now,
            "10.0.0.2",
            "PC2",
            false
        )
        `when`(sessionRegistry.findActiveSessions(iamUserId, iamTenantId))
            .thenReturn(listOf(olderSession, newestSession))

        val result = service.enforceSingleSession(studentIdStr, tenantId, null)

        assertThat(result.totalFound).isEqualTo(2)
        assertThat(result.terminatedCount).isEqualTo(1)
        assertThat(result.retainedTokenIdentifier).isEqualTo("newest-token")

        verify(sessionRegistry).revokeSession(olderSession.id())
        verify(tokenRevocationPort).revokeToken("older-token", olderSession.expiresAt())
    }

    @Test
    fun `terminateAllStudentSessions should revoke all sessions and tokens`() {
        `when`(sessionRegistry.countActiveSessions(iamUserId, iamTenantId)).thenReturn(3)

        val revokedCount = service.terminateAllStudentSessions(studentIdStr, tenantId)

        assertThat(revokedCount).isEqualTo(3)
        verify(sessionRegistry).revokeAllSessions(iamUserId, iamTenantId)
        verify(tokenRevocationPort).revokeAllForUser(org.mockito.ArgumentMatchers.eq(iamUserId), org.mockito.ArgumentMatchers.any())
    }
}
