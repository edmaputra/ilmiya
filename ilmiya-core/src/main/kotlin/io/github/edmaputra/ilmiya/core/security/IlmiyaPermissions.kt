package io.github.edmaputra.ilmiya.core.security

/**
 * Standard fine-grained permission constants across Ilmiya school bounded contexts.
 *
 * All permissions follow the `{domain}:{resource}:{action}` notation to leverage
 * ed-iam domain wildcard matching (e.g. `academic:*`, `student:*`, `attendance:*`).
 *
 * Note: IAM management endpoints (Users, Roles, Groups, Scopes) are handled directly
 * by the ed-iam library using `@RequirePermission`.
 *
 * @author edmaputra
 * @since 0.0.1
 */
object IlmiyaPermissions {

    // School Context
    const val SCHOOL_REGISTER = "school:registration:create"
    const val SCHOOL_PROFILE_READ = "school:profile:read"
    const val SCHOOL_PROFILE_WRITE = "school:profile:write"
    const val SCHOOL_SETTINGS_MANAGE = "school:settings:manage"

    // Tenant Management Context
    const val TENANT_READ = "tenant:management:read"
    const val TENANT_CREATE = "tenant:management:create"
    const val TENANT_UPDATE = "tenant:management:update"
    const val TENANT_DELETE = "tenant:management:delete"

    // Academic Context
    const val ACADEMIC_YEAR_READ = "academic:year:read"
    const val ACADEMIC_YEAR_WRITE = "academic:year:write"
    const val CURRICULUM_READ = "academic:curriculum:read"
    const val CURRICULUM_MANAGE = "academic:curriculum:manage"
    const val COURSE_READ = "academic:course:read"
    const val COURSE_WRITE = "academic:course:write"

    // Student Context
    const val STUDENT_PROFILE_READ = "student:profile:read"
    const val STUDENT_PROFILE_WRITE = "student:profile:write"
    const val STUDENT_ENROLLMENT_MANAGE = "student:enrollment:manage"
    const val GUARDIAN_LINK_MANAGE = "student:guardian:manage"

    // Attendance Context
    const val ATTENDANCE_READ = "attendance:record:read"
    const val ATTENDANCE_RECORD = "attendance:record:create"
    const val ATTENDANCE_MODIFY = "attendance:record:update"
    const val ATTENDANCE_APPROVE = "attendance:record:approve"

    // Human Resources Context
    const val STAFF_PROFILE_READ = "hr:staff:read"
    const val STAFF_PROFILE_WRITE = "hr:staff:write"
    const val TEACHER_ASSIGNMENT_MANAGE = "hr:assignment:manage"

    // Timetable Context
    const val TIMETABLE_READ = "timetable:schedule:read"
    const val TIMETABLE_MANAGE = "timetable:schedule:manage"

    // Examination Context
    const val EXAM_SCHEDULE_READ = "exam:schedule:read"
    const val EXAM_SCHEDULE_MANAGE = "exam:schedule:manage"
    const val GRADE_ENTRY = "exam:grade:create"
    const val GRADE_MODIFY = "exam:grade:update"
    const val REPORT_CARD_PUBLISH = "exam:report-card:publish"

    // Finance Context
    const val FEE_STRUCTURE_MANAGE = "finance:fee:manage"
    const val INVOICE_CREATE = "finance:invoice:create"
    const val PAYMENT_RECORD = "finance:payment:create"
    const val FINANCIAL_REPORT_READ = "finance:report:read"
}
