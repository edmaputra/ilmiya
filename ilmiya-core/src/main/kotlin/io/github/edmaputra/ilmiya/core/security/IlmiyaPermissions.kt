package io.github.edmaputra.ilmiya.core.security

/**
 * Standard fine-grained permission constants across Ilmiya bounded contexts.
 *
 * @author edmaputra
 */
object IlmiyaPermissions {

    // Academic Context
    const val ACADEMIC_YEAR_READ = "ACADEMIC_YEAR_READ"
    const val ACADEMIC_YEAR_WRITE = "ACADEMIC_YEAR_WRITE"
    const val CURRICULUM_READ = "CURRICULUM_READ"
    const val CURRICULUM_MANAGE = "CURRICULUM_MANAGE"
    const val COURSE_READ = "COURSE_READ"
    const val COURSE_WRITE = "COURSE_WRITE"

    // Student Context
    const val STUDENT_PROFILE_READ = "STUDENT_PROFILE_READ"
    const val STUDENT_PROFILE_WRITE = "STUDENT_PROFILE_WRITE"
    const val STUDENT_ENROLLMENT_MANAGE = "STUDENT_ENROLLMENT_MANAGE"
    const val GUARDIAN_LINK_MANAGE = "GUARDIAN_LINK_MANAGE"

    // Attendance Context
    const val ATTENDANCE_READ = "ATTENDANCE_READ"
    const val ATTENDANCE_RECORD = "ATTENDANCE_RECORD"
    const val ATTENDANCE_MODIFY = "ATTENDANCE_MODIFY"
    const val ATTENDANCE_APPROVE = "ATTENDANCE_APPROVE"

    // Human Resources Context
    const val STAFF_PROFILE_READ = "STAFF_PROFILE_READ"
    const val STAFF_PROFILE_WRITE = "STAFF_PROFILE_WRITE"
    const val TEACHER_ASSIGNMENT_MANAGE = "TEACHER_ASSIGNMENT_MANAGE"

    // Timetable Context
    const val TIMETABLE_READ = "TIMETABLE_READ"
    const val TIMETABLE_MANAGE = "TIMETABLE_MANAGE"

    // Examination Context
    const val EXAM_SCHEDULE_READ = "EXAM_SCHEDULE_READ"
    const val EXAM_SCHEDULE_MANAGE = "EXAM_SCHEDULE_MANAGE"
    const val GRADE_ENTRY = "GRADE_ENTRY"
    const val GRADE_MODIFY = "GRADE_MODIFY"
    const val REPORT_CARD_PUBLISH = "REPORT_CARD_PUBLISH"

    // Finance Context
    const val FEE_STRUCTURE_MANAGE = "FEE_STRUCTURE_MANAGE"
    const val INVOICE_CREATE = "INVOICE_CREATE"
    const val PAYMENT_RECORD = "PAYMENT_RECORD"
    const val FINANCIAL_REPORT_READ = "FINANCIAL_REPORT_READ"

    // IAM & Tenant Administration
    const val IAM_USER_MANAGE = "IAM_USER_MANAGE"
    const val IAM_ROLE_MANAGE = "IAM_ROLE_MANAGE"
    const val IAM_GROUP_MANAGE = "IAM_GROUP_MANAGE"
    const val IAM_SCOPE_MANAGE = "IAM_SCOPE_MANAGE"
}
