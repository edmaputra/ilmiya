package io.github.edmaputra.ilmiya.core.security

/**
 * Standard fine-grained permission constants across Ilmiya school bounded contexts.
 *
 * All permissions follow the `{domain}:{resource}:{action}` notation to leverage
 * ed-iam domain wildcard matching (e.g. `tenant:*`, `school:*`, `academic:*`, `student:*`, `attendance:*`).
 *
 * Note: IAM management endpoints (Users, Roles, Groups, Scopes) are handled directly
 * by the ed-iam library using `@RequirePermission`.
 *
 * @author edmaputra
 * @since 0.0.1
 */
object IlmiyaPermissions {

    // Tenant & Foundation Context
    const val TENANT_READ = "tenant:management:read"
    const val TENANT_CREATE = "tenant:management:create"
    const val TENANT_UPDATE = "tenant:management:update"
    const val TENANT_DELETE = "tenant:management:delete"
    const val TENANT_PROFILE_READ = "tenant:profile:read"
    const val TENANT_PROFILE_WRITE = "tenant:profile:write"
    const val TENANT_SCHOOL_MANAGE = "tenant:school:manage"
    const val TENANT_SUBSCRIPTION_READ = "tenant:subscription:read"
    const val TENANT_AUDIT_READ = "tenant:audit:read"

    // School Context
    const val SCHOOL_PROFILE_READ = "school:profile:read"
    const val SCHOOL_PROFILE_WRITE = "school:profile:write"
    const val SCHOOL_SETTINGS_MANAGE = "school:settings:manage"
    const val SCHOOL_USER_MANAGE = "school:user:manage"
    const val SCHOOL_AUDIT_READ = "school:audit:read"

    // Academic Context
    const val ACADEMIC_YEAR_READ = "academic:year:read"
    const val ACADEMIC_YEAR_WRITE = "academic:year:write"
    const val ACADEMIC_TERM_READ = "academic:term:read"
    const val ACADEMIC_TERM_WRITE = "academic:term:write"
    const val CURRICULUM_READ = "academic:curriculum:read"
    const val CURRICULUM_MANAGE = "academic:curriculum:manage"
    const val COURSE_READ = "academic:course:read"
    const val COURSE_WRITE = "academic:course:write"
    const val CLASS_READ = "academic:class:read"
    const val CLASS_WRITE = "academic:class:write"
    const val PROMOTION_MANAGE = "academic:promotion:manage"

    // Student Context
    const val STUDENT_PROFILE_READ = "student:profile:read"
    const val STUDENT_PROFILE_WRITE = "student:profile:write"
    const val STUDENT_ADMISSION_MANAGE = "student:admission:manage"
    const val STUDENT_ENROLLMENT_MANAGE = "student:enrollment:manage"
    const val GUARDIAN_LINK_MANAGE = "student:guardian:manage"
    const val STUDENT_DOCUMENT_READ = "student:document:read"
    const val STUDENT_DOCUMENT_WRITE = "student:document:write"
    const val STUDENT_CONDUCT_READ = "student:conduct:read"
    const val STUDENT_CONDUCT_MANAGE = "student:conduct:manage"

    // Attendance Context
    const val ATTENDANCE_READ = "attendance:record:read"
    const val ATTENDANCE_RECORD = "attendance:record:create"
    const val ATTENDANCE_MODIFY = "attendance:record:update"
    const val ATTENDANCE_APPROVE = "attendance:record:approve"
    const val ATTENDANCE_STUDENT_READ = "attendance:student:read"
    const val ATTENDANCE_STUDENT_RECORD = "attendance:student:record"
    const val ATTENDANCE_STUDENT_UPDATE = "attendance:student:update"
    const val ATTENDANCE_STUDENT_EXCUSE = "attendance:student:excuse"
    const val ATTENDANCE_STAFF_READ = "attendance:staff:read"
    const val ATTENDANCE_STAFF_RECORD = "attendance:staff:record"
    const val ATTENDANCE_STAFF_APPROVE = "attendance:staff:approve"

    // Timetable Context
    const val TIMETABLE_READ = "timetable:schedule:read"
    const val TIMETABLE_MANAGE = "timetable:schedule:manage"
    const val TIMETABLE_CALENDAR_READ = "timetable:calendar:read"
    const val TIMETABLE_CALENDAR_MANAGE = "timetable:calendar:manage"

    // Examination Context
    const val EXAM_SCHEDULE_READ = "exam:schedule:read"
    const val EXAM_SCHEDULE_MANAGE = "exam:schedule:manage"
    const val GRADE_READ = "exam:grade:read"
    const val GRADE_ENTRY = "exam:grade:create"
    const val GRADE_MODIFY = "exam:grade:update"
    const val GRADE_LOCK = "exam:grade:lock"
    const val REPORT_CARD_READ = "exam:report-card:read"
    const val REPORT_CARD_GENERATE = "exam:report-card:generate"
    const val REPORT_CARD_PUBLISH = "exam:report-card:publish"

    // Finance Context
    const val FEE_STRUCTURE_READ = "finance:fee:read"
    const val FEE_STRUCTURE_MANAGE = "finance:fee:manage"
    const val INVOICE_READ = "finance:invoice:read"
    const val INVOICE_CREATE = "finance:invoice:create"
    const val PAYMENT_READ = "finance:payment:read"
    const val PAYMENT_RECORD = "finance:payment:create"
    const val PAYMENT_VOID = "finance:payment:void"
    const val FINANCIAL_REPORT_READ = "finance:report:read"

    // Human Resources Context
    const val STAFF_PROFILE_READ = "hr:staff:read"
    const val STAFF_PROFILE_WRITE = "hr:staff:write"
    const val TEACHER_ASSIGNMENT_READ = "hr:assignment:read"
    const val TEACHER_ASSIGNMENT_MANAGE = "hr:assignment:manage"
    const val STAFF_LEAVE_REQUEST = "hr:leave:request"
    const val STAFF_LEAVE_APPROVE = "hr:leave:approve"
    const val PAYROLL_READ = "hr:payroll:read"
    const val PAYROLL_CALCULATE = "hr:payroll:calculate"
    const val PAYROLL_DISBURSE = "hr:payroll:disburse"

    // Audit Context
    const val AUDIT_LOG_READ = "audit:log:read"
}
