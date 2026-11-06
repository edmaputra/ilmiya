package io.github.edmaputra.ilmiya.core.security.session

import io.github.edmaputra.iam.domain.model.UserSession
import java.time.Instant

/**
 * Representation of an active user authentication session for school and exam monitoring.
 *
 * @property sessionId Unique identifier of the session
 * @property tokenIdentifier JWT token identifier (jti)
 * @property createdAt Timestamp when session was initiated
 * @property expiresAt Timestamp when session expires
 * @property lastAccessAt Timestamp of most recent activity
 * @property ipAddress Client IP address if recorded
 * @property userAgent Client browser/device user-agent if recorded
 *
 * @author edmaputra
 * @since 0.1.0
 */
data class StudentSessionDto(
    val sessionId: String,
    val tokenIdentifier: String,
    val createdAt: Instant,
    val expiresAt: Instant,
    val lastAccessAt: Instant,
    val ipAddress: String?,
    val userAgent: String?
) {
    companion object {
        fun from(session: UserSession): StudentSessionDto =
            StudentSessionDto(
                sessionId = session.id().value().toString(),
                tokenIdentifier = session.tokenIdentifier(),
                createdAt = session.createdAt(),
                expiresAt = session.expiresAt(),
                lastAccessAt = session.lastAccessAt(),
                ipAddress = session.ipAddress(),
                userAgent = session.userAgent()
            )
    }
}

/**
 * Result payload following an exam session lockdown execution.
 *
 * @property studentUserId The student user ID
 * @property tenantId The school tenant ID
 * @property totalFound Total active sessions located before lockdown
 * @property terminatedCount Total secondary sessions forcibly revoked
 * @property retainedTokenIdentifier The single token identifier permitted to remain active
 *
 * @author edmaputra
 * @since 0.1.0
 */
data class ExamLockdownResult(
    val studentUserId: String,
    val tenantId: String,
    val totalFound: Int,
    val terminatedCount: Int,
    val retainedTokenIdentifier: String?
)

/**
 * Request body for enforcing single-session lockdown on a student.
 *
 * @property currentTokenIdentifier Optional token ID (jti) to retain (if omitted, the newest session is retained)
 */
data class EnforceSingleSessionRequest(
    val currentTokenIdentifier: String? = null
)

/**
 * Summary count of active sessions for a student.
 */
data class ActiveSessionCountResponse(
    val studentUserId: String,
    val activeSessionCount: Int
)
