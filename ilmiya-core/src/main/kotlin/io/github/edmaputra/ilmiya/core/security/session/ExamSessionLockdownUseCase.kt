package io.github.edmaputra.ilmiya.core.security.session

import io.github.edmaputra.ilmiya.common.domain.TenantId

/**
 * Use case contract defining session concurrency control and exam integrity lockdown operations.
 *
 * Designed to guarantee that candidates appearing for examinations operate under a strict
 * single-device session constraint, terminating any proxy or simultaneous logins.
 *
 * @author edmaputra
 * @since 0.1.0
 */
interface ExamSessionLockdownUseCase {

    /**
     * Retrieves the count of currently active sessions for a student under the specified tenant.
     *
     * @param studentUserId the student user ID
     * @param tenantId the school tenant context
     * @return count of active non-revoked sessions
     */
    fun getActiveSessionCount(studentUserId: String, tenantId: TenantId): Int

    /**
     * Lists active session details for a student under the specified tenant.
     *
     * @param studentUserId the student user ID
     * @param tenantId the school tenant context
     * @return list of active session DTOs
     */
    fun listStudentSessions(studentUserId: String, tenantId: TenantId): List<StudentSessionDto>

    /**
     * Enforces single-device lockdown for an examination session.
     *
     * If multiple active sessions exist for the student, all sessions other than the
     * retained token (or newest session if unspecified) are revoked immediately in both
     * the session registry and the real-time token denylist.
     *
     * @param studentUserId the student user ID
     * @param tenantId the school tenant context
     * @param keepTokenIdentifier optional token identifier to keep active
     * @return summary outcome of the lockdown action
     */
    fun enforceSingleSession(
        studentUserId: String,
        tenantId: TenantId,
        keepTokenIdentifier: String? = null
    ): ExamLockdownResult

    /**
     * Forcibly revokes all active sessions for a student across all devices.
     *
     * Used by proctors when irregular behavior or compromised credentials are identified.
     *
     * @param studentUserId the student user ID
     * @param tenantId the school tenant context
     * @return number of sessions revoked
     */
    fun terminateAllStudentSessions(studentUserId: String, tenantId: TenantId): Int
}
