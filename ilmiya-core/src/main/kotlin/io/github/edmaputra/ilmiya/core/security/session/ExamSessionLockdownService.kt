package io.github.edmaputra.ilmiya.core.security.session

import io.github.edmaputra.iam.application.port.out.SessionRegistryPort
import io.github.edmaputra.iam.application.port.out.TokenRevocationPort
import io.github.edmaputra.iam.domain.model.UserId
import io.github.edmaputra.iam.domain.tenancy.TenantId as IamTenantId
import io.github.edmaputra.ilmiya.common.domain.TenantId
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.UUID

/**
 * Service orchestrating student session inspection, concurrency lockdown, and forced revocation
 * for exam integrity management.
 *
 * Interacts directly with ed-iam's [SessionRegistryPort] and [TokenRevocationPort] to ensure
 * instantaneous denylisting of invalidated tokens across virtual threads and API requests.
 *
 * @author edmaputra
 * @since 0.1.0
 */
@Service
class ExamSessionLockdownService(
    private val sessionRegistry: SessionRegistryPort,
    private val tokenRevocationPort: TokenRevocationPort
) : ExamSessionLockdownUseCase {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun getActiveSessionCount(studentUserId: String, tenantId: TenantId): Int {
        val (iamUserId, iamTenantId) = resolveIamIds(studentUserId, tenantId)
        return sessionRegistry.countActiveSessions(iamUserId, iamTenantId)
    }

    override fun listStudentSessions(studentUserId: String, tenantId: TenantId): List<StudentSessionDto> {
        val (iamUserId, iamTenantId) = resolveIamIds(studentUserId, tenantId)
        return sessionRegistry.findActiveSessions(iamUserId, iamTenantId)
            .map { StudentSessionDto.from(it) }
    }

    override fun enforceSingleSession(
        studentUserId: String,
        tenantId: TenantId,
        keepTokenIdentifier: String?
    ): ExamLockdownResult {
        val (iamUserId, iamTenantId) = resolveIamIds(studentUserId, tenantId)
        val activeSessions = sessionRegistry.findActiveSessions(iamUserId, iamTenantId)
        val totalFound = activeSessions.size

        if (totalFound <= 1) {
            log.info(
                "Student '{}' in tenant '{}' has {} active session(s). No lockdown eviction required.",
                studentUserId, tenantId.value, totalFound
            )
            return ExamLockdownResult(
                studentUserId = studentUserId,
                tenantId = tenantId.value,
                totalFound = totalFound,
                terminatedCount = 0,
                retainedTokenIdentifier = activeSessions.firstOrNull()?.tokenIdentifier()
            )
        }

        // Determine session to retain
        val retainedSession = if (!keepTokenIdentifier.isNullOrBlank()) {
            activeSessions.find { it.tokenIdentifier() == keepTokenIdentifier }
                ?: activeSessions.maxByOrNull { it.createdAt() }!!
        } else {
            activeSessions.maxByOrNull { it.createdAt() }!!
        }

        val sessionsToRevoke = activeSessions.filter { it.id() != retainedSession.id() }
        var terminatedCount = 0

        for (session in sessionsToRevoke) {
            sessionRegistry.revokeSession(session.id())
            tokenRevocationPort.revokeToken(session.tokenIdentifier(), session.expiresAt())
            terminatedCount++
            log.warn(
                "Revoked duplicate exam session '{}' (token: '{}', IP: '{}') for student '{}'",
                session.id().value(), session.tokenIdentifier(), session.ipAddress(), studentUserId
            )
        }

        log.info(
            "Exam lockdown enforced for student '{}': {}/{} sessions terminated. Retained token '{}'",
            studentUserId, terminatedCount, totalFound, retainedSession.tokenIdentifier()
        )

        return ExamLockdownResult(
            studentUserId = studentUserId,
            tenantId = tenantId.value,
            totalFound = totalFound,
            terminatedCount = terminatedCount,
            retainedTokenIdentifier = retainedSession.tokenIdentifier()
        )
    }

    override fun terminateAllStudentSessions(studentUserId: String, tenantId: TenantId): Int {
        val (iamUserId, iamTenantId) = resolveIamIds(studentUserId, tenantId)
        val activeCount = sessionRegistry.countActiveSessions(iamUserId, iamTenantId)

        sessionRegistry.revokeAllSessions(iamUserId, iamTenantId)
        tokenRevocationPort.revokeAllForUser(iamUserId, Instant.now())

        log.warn(
            "Forcibly terminated all ({}) active sessions for student '{}' in tenant '{}'",
            activeCount, studentUserId, tenantId.value
        )
        return activeCount
    }

    private fun resolveIamIds(studentUserId: String, tenantId: TenantId): Pair<UserId, IamTenantId> {
        val iamUserId = UserId(UUID.fromString(studentUserId))
        val iamTenantId = IamTenantId.from(tenantId.value)
        return iamUserId to iamTenantId
    }
}
