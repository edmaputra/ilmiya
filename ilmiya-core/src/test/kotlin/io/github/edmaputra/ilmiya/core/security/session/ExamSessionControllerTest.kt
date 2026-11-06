package io.github.edmaputra.ilmiya.core.security.session

import io.github.edmaputra.ilmiya.common.context.TenantContext
import io.github.edmaputra.ilmiya.common.domain.TenantId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.springframework.http.HttpStatus
import java.time.Instant
import java.util.UUID

class ExamSessionControllerTest {

    private lateinit var useCase: ExamSessionLockdownUseCase
    private lateinit var controller: ExamSessionController

    private val tenantUuid = UUID.randomUUID().toString()
    private val tenantId = TenantId.of(tenantUuid)
    private val studentId = UUID.randomUUID().toString()

    @BeforeEach
    fun setUp() {
        useCase = mock(ExamSessionLockdownUseCase::class.java)
        controller = ExamSessionController(useCase)
    }

    @Test
    fun `getActiveSessionCount should return active count from usecase`() {
        TenantContext.withTenant(tenantUuid) {
            `when`(useCase.getActiveSessionCount(studentId, tenantId)).thenReturn(2)

            val response = controller.getActiveSessionCount(studentId)

            assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
            assertThat(response.body?.studentUserId).isEqualTo(studentId)
            assertThat(response.body?.activeSessionCount).isEqualTo(2)
            verify(useCase).getActiveSessionCount(studentId, tenantId)
        }
    }

    @Test
    fun `listStudentSessions should return sessions from usecase`() {
        TenantContext.withTenant(tenantUuid) {
            val now = Instant.now()
            val sessionDto = StudentSessionDto(
                sessionId = "sess-1",
                tokenIdentifier = "token-1",
                createdAt = now,
                expiresAt = now.plusSeconds(3600),
                lastAccessAt = now,
                ipAddress = "10.0.0.1",
                userAgent = "Browser"
            )
            `when`(useCase.listStudentSessions(studentId, tenantId)).thenReturn(listOf(sessionDto))

            val response = controller.listStudentSessions(studentId)

            assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
            assertThat(response.body).hasSize(1)
            assertThat(response.body?.get(0)?.tokenIdentifier).isEqualTo("token-1")
            verify(useCase).listStudentSessions(studentId, tenantId)
        }
    }

    @Test
    fun `enforceSingleSession should delegate to usecase with request token`() {
        TenantContext.withTenant(tenantUuid) {
            val result = ExamLockdownResult(
                studentUserId = studentId,
                tenantId = tenantUuid,
                totalFound = 2,
                terminatedCount = 1,
                retainedTokenIdentifier = "token-exam"
            )
            `when`(useCase.enforceSingleSession(studentId, tenantId, "token-exam")).thenReturn(result)

            val request = EnforceSingleSessionRequest(currentTokenIdentifier = "token-exam")
            val response = controller.enforceSingleSession(studentId, request)

            assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
            assertThat(response.body?.terminatedCount).isEqualTo(1)
            assertThat(response.body?.retainedTokenIdentifier).isEqualTo("token-exam")
            verify(useCase).enforceSingleSession(studentId, tenantId, "token-exam")
        }
    }

    @Test
    fun `terminateAllSessions should delegate to usecase and return confirmation`() {
        TenantContext.withTenant(tenantUuid) {
            `when`(useCase.terminateAllStudentSessions(studentId, tenantId)).thenReturn(3)

            val response = controller.terminateAllSessions(studentId)

            assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
            assertThat(response.body?.get("revokedSessionsCount")).isEqualTo(3)
            assertThat(response.body?.get("studentUserId")).isEqualTo(studentId)
            verify(useCase).terminateAllStudentSessions(studentId, tenantId)
        }
    }
}
