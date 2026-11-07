package io.github.edmaputra.ilmiya.core.security.session

import io.github.edmaputra.iam.domain.security.annotation.RequirePermission
import io.github.edmaputra.ilmiya.common.context.TenantContext
import io.github.edmaputra.ilmiya.core.security.IlmiyaPermissions
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * REST controller for exam session inspection, concurrency lockdown, and forced termination.
 *
 * Provides proctors, invigilators, and academic supervisors with the ability to monitor and
 * enforce single-device session integrity during examination windows.
 *
 * @author edmaputra
 * @since 0.1.0
 */
@RestController
@RequestMapping("/api/v1/exam-sessions")
class ExamSessionController(
    private val examSessionLockdownUseCase: ExamSessionLockdownUseCase
) {

    /**
     * Retrieves the count of active sessions for a student.
     */
    @GetMapping("/{studentId}/count")
    @RequirePermission(IlmiyaPermissions.EXAM_SCHEDULE_READ)
    fun getActiveSessionCount(
        @PathVariable studentId: String
    ): ResponseEntity<ActiveSessionCountResponse> {
        val tenantId = TenantContext.requireTenantId()
        val count = examSessionLockdownUseCase.getActiveSessionCount(studentId, tenantId)
        return ResponseEntity.ok(ActiveSessionCountResponse(studentUserId = studentId, activeSessionCount = count))
    }

    /**
     * Lists all active authentication sessions for a student.
     */
    @GetMapping("/{studentId}")
    @RequirePermission(IlmiyaPermissions.EXAM_SCHEDULE_READ)
    fun listStudentSessions(
        @PathVariable studentId: String
    ): ResponseEntity<List<StudentSessionDto>> {
        val tenantId = TenantContext.requireTenantId()
        val sessions = examSessionLockdownUseCase.listStudentSessions(studentId, tenantId)
        return ResponseEntity.ok(sessions)
    }

    /**
     * Enforces single-device lockdown for a student, revoking all secondary sessions.
     */
    @PostMapping("/{studentId}/lockdown")
    @RequirePermission(IlmiyaPermissions.EXAM_SCHEDULE_UPDATE)
    fun enforceSingleSession(
        @PathVariable studentId: String,
        @RequestBody(required = false) request: EnforceSingleSessionRequest?
    ): ResponseEntity<ExamLockdownResult> {
        val tenantId = TenantContext.requireTenantId()
        val result = examSessionLockdownUseCase.enforceSingleSession(
            studentUserId = studentId,
            tenantId = tenantId,
            keepTokenIdentifier = request?.currentTokenIdentifier
        )
        return ResponseEntity.ok(result)
    }

    /**
     * Forcibly revokes all active sessions for a student across all devices.
     */
    @PostMapping("/{studentId}/terminate-all")
    @RequirePermission(IlmiyaPermissions.EXAM_SCHEDULE_UPDATE)
    fun terminateAllSessions(
        @PathVariable studentId: String
    ): ResponseEntity<Map<String, Any>> {
        val tenantId = TenantContext.requireTenantId()
        val revokedCount = examSessionLockdownUseCase.terminateAllStudentSessions(studentId, tenantId)
        return ResponseEntity.ok(
            mapOf(
                "studentUserId" to studentId,
                "tenantId" to tenantId.value,
                "revokedSessionsCount" to revokedCount,
                "message" to "All active sessions have been terminated."
            )
        )
    }
}
