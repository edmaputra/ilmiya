package io.github.edmaputra.ilmiya.core.security

/**
 * Standard fine-grained permission constants across Ilmiya school bounded contexts.
 *
 * Implements a **Hybrid Authorization Model**:
 * 1. **Category A (Core Master CRUD Entities)**: Fine-grained `:read`, `:create`, `:update`, `:delete` for persistent entities with data-loss risk.
 * 2. **Category B (High-Risk Segregation of Duties)**: Explicit dual-control action verbs (`:approve`, `:lock`, `:publish`, `:void`) for financial, grading, and leave sign-offs.
 * 3. **Category C (Multi-Step Business Workflows)**: Lifecycle action verbs (`:apply`, `:review`, `:execute`, `:rollback`, `:calculate`, `:disburse`) for procedural state machines.
 * 4. **Category D (Singletons, Settings & Orchestration)**: Focused `:read` and `:manage` / `:update` for single-instance settings and relational mappings.
 *
 * All permissions follow `{domain}:{resource}:{action}` syntax to leverage
 * ed-iam domain wildcard matching (e.g. `tenant:*`, `school:*`, `academic:course:*`).
 *
 * @author edmaputra
 * @since 0.0.1
 */
object IlmiyaPermissions {

    // =========================================================================
    // Tenant & Foundation Context (tenant:*)
    // =========================================================================
    const val TENANT_READ = "tenant:management:read"
    const val TENANT_CREATE = "tenant:management:create"
    const val TENANT_UPDATE = "tenant:management:update"
    const val TENANT_DELETE = "tenant:management:delete"
    const val TENANT_PROFILE_READ = "tenant:profile:read"
    const val TENANT_PROFILE_WRITE = "tenant:profile:write"
    const val TENANT_PROFILE_UPDATE = "tenant:profile:update"
    const val TENANT_SCHOOL_MANAGE = "tenant:school:manage"
    const val TENANT_SUBSCRIPTION_READ = "tenant:subscription:read"
    const val TENANT_AUDIT_READ = "tenant:audit:read"

    // =========================================================================
    // School Profile, Settings & Users Context (school:*)
    // =========================================================================
    // School Profile (Category D)
    const val SCHOOL_PROFILE_READ = "school:profile:read"
    const val SCHOOL_PROFILE_UPDATE = "school:profile:update"
    const val SCHOOL_PROFILE_WRITE = SCHOOL_PROFILE_UPDATE // Legacy alias for backward compatibility

    // School Operational Settings (Category D - Singleton)
    const val SCHOOL_SETTINGS_READ = "school:settings:read"
    const val SCHOOL_SETTINGS_MANAGE = "school:settings:manage"
    const val SCHOOL_SETTINGS_UPDATE = "school:settings:update"

    // School Users (Category A - Master CRUD)
    const val SCHOOL_USER_READ = "school:user:read"
    const val SCHOOL_USER_CREATE = "school:user:create"
    const val SCHOOL_USER_UPDATE = "school:user:update"
    const val SCHOOL_USER_DELETE = "school:user:delete"
    const val SCHOOL_USER_MANAGE = "school:user:manage" // Legacy alias

    // School Audit
    const val SCHOOL_AUDIT_READ = "school:audit:read"

    // =========================================================================
    // Academic Structure & Curriculum Context (academic:*)
    // =========================================================================
    // Academic Year (Category A - Master CRUD)
    const val ACADEMIC_YEAR_READ = "academic:year:read"
    const val ACADEMIC_YEAR_CREATE = "academic:year:create"
    const val ACADEMIC_YEAR_UPDATE = "academic:year:update"
    const val ACADEMIC_YEAR_DELETE = "academic:year:delete"
    const val ACADEMIC_YEAR_WRITE = "academic:year:write" // Legacy alias

    // Academic Term / Semester (Category A - Master CRUD)
    const val ACADEMIC_TERM_READ = "academic:term:read"
    const val ACADEMIC_TERM_CREATE = "academic:term:create"
    const val ACADEMIC_TERM_UPDATE = "academic:term:update"
    const val ACADEMIC_TERM_DELETE = "academic:term:delete"
    const val ACADEMIC_TERM_WRITE = "academic:term:write" // Legacy alias

    // Curriculum (Category D - Configuration)
    const val CURRICULUM_READ = "academic:curriculum:read"
    const val CURRICULUM_MANAGE = "academic:curriculum:manage"

    // Subject Course Catalog (Category A - Master CRUD)
    const val COURSE_READ = "academic:course:read"
    const val COURSE_CREATE = "academic:course:create"
    const val COURSE_UPDATE = "academic:course:update"
    const val COURSE_DELETE = "academic:course:delete"
    const val COURSE_WRITE = "academic:course:write" // Legacy alias

    // Classes / Batch Sections (Category A - Master CRUD)
    const val CLASS_READ = "academic:class:read"
    const val CLASS_CREATE = "academic:class:create"
    const val CLASS_UPDATE = "academic:class:update"
    const val CLASS_DELETE = "academic:class:delete"
    const val CLASS_WRITE = "academic:class:write" // Legacy alias

    // Year-End Class Promotion Workflow (Category C - Lifecycle)
    const val PROMOTION_READ = "academic:promotion:read"
    const val PROMOTION_EXECUTE = "academic:promotion:execute"
    const val PROMOTION_ROLLBACK = "academic:promotion:rollback"
    const val PROMOTION_MANAGE = "academic:promotion:manage" // Legacy alias

    // =========================================================================
    // Student Lifecycle & Admissions Context (student:*)
    // =========================================================================
    // Student Master Profile (Category A - Master CRUD)
    const val STUDENT_PROFILE_READ = "student:profile:read"
    const val STUDENT_PROFILE_CREATE = "student:profile:create"
    const val STUDENT_PROFILE_UPDATE = "student:profile:update"
    const val STUDENT_PROFILE_DELETE = "student:profile:delete"
    const val STUDENT_PROFILE_WRITE = "student:profile:write" // Legacy alias

    // Student Admissions / PPDB (Category C - Lifecycle)
    const val STUDENT_ADMISSION_READ = "student:admission:read"
    const val STUDENT_ADMISSION_APPLY = "student:admission:apply"
    const val STUDENT_ADMISSION_REVIEW = "student:admission:review"
    const val STUDENT_ADMISSION_APPROVE = "student:admission:approve"
    const val STUDENT_ADMISSION_MANAGE = "student:admission:manage" // Legacy alias

    // Class Section Enrollment (Category D - Relational Mapping)
    const val STUDENT_ENROLLMENT_MANAGE = "student:enrollment:manage"

    // Guardian Linkage (Category D - Relational Mapping)
    const val GUARDIAN_LINK_MANAGE = "student:guardian:manage"

    // Student Compliance Documents (Category A - Document CRUD)
    const val STUDENT_DOCUMENT_READ = "student:document:read"
    const val STUDENT_DOCUMENT_UPLOAD = "student:document:upload"
    const val STUDENT_DOCUMENT_DELETE = "student:document:delete"
    const val STUDENT_DOCUMENT_WRITE = "student:document:write" // Legacy alias

    // Student Conduct & Discipline (Category B/A)
    const val STUDENT_CONDUCT_READ = "student:conduct:read"
    const val STUDENT_CONDUCT_RECORD = "student:conduct:record"
    const val STUDENT_CONDUCT_UPDATE = "student:conduct:update"
    const val STUDENT_CONDUCT_DELETE = "student:conduct:delete"
    const val STUDENT_CONDUCT_MANAGE = "student:conduct:manage" // Legacy alias

    // =========================================================================
    // Attendance Tracking Context (attendance:*)
    // =========================================================================
    // Student Attendance (Category B - Segregated Justification)
    const val ATTENDANCE_READ = "attendance:record:read"
    const val ATTENDANCE_RECORD = "attendance:record:create"
    const val ATTENDANCE_MODIFY = "attendance:record:update"
    const val ATTENDANCE_APPROVE = "attendance:record:approve"
    const val ATTENDANCE_STUDENT_READ = "attendance:student:read"
    const val ATTENDANCE_STUDENT_RECORD = "attendance:student:record"
    const val ATTENDANCE_STUDENT_UPDATE = "attendance:student:update"
    const val ATTENDANCE_STUDENT_EXCUSE = "attendance:student:excuse"

    // Staff Attendance (Category B - Supervisor Approval)
    const val ATTENDANCE_STAFF_READ = "attendance:staff:read"
    const val ATTENDANCE_STAFF_RECORD = "attendance:staff:record"
    const val ATTENDANCE_STAFF_APPROVE = "attendance:staff:approve"

    // =========================================================================
    // Timetable & Calendar Context (timetable:*)
    // =========================================================================
    // Timetable Schedules (Category A/B)
    const val TIMETABLE_READ = "timetable:schedule:read"
    const val TIMETABLE_SCHEDULE_READ = "timetable:schedule:read"
    const val TIMETABLE_SCHEDULE_CREATE = "timetable:schedule:create"
    const val TIMETABLE_SCHEDULE_UPDATE = "timetable:schedule:update"
    const val TIMETABLE_SCHEDULE_DELETE = "timetable:schedule:delete"
    const val TIMETABLE_SCHEDULE_PUBLISH = "timetable:schedule:publish"
    const val TIMETABLE_MANAGE = "timetable:schedule:manage" // Legacy alias

    // School Events & Holiday Calendar (Category D - Orchestration)
    const val TIMETABLE_CALENDAR_READ = "timetable:calendar:read"
    const val TIMETABLE_CALENDAR_MANAGE = "timetable:calendar:manage"

    // =========================================================================
    // Examination, Assessment & e-Rapor Context (exam:*)
    // =========================================================================
    // Exam Sessions (Category A - Master CRUD)
    const val EXAM_SCHEDULE_READ = "exam:schedule:read"
    const val EXAM_SCHEDULE_CREATE = "exam:schedule:create"
    const val EXAM_SCHEDULE_UPDATE = "exam:schedule:update"
    const val EXAM_SCHEDULE_DELETE = "exam:schedule:delete"
    const val EXAM_SCHEDULE_MANAGE = "exam:schedule:manage" // Legacy alias

    // Grade Entry (Category B - Dual Control Lock)
    const val GRADE_READ = "exam:grade:read"
    const val GRADE_ENTRY = "exam:grade:create"
    const val GRADE_MODIFY = "exam:grade:update"
    const val GRADE_LOCK = "exam:grade:lock"

    // e-Rapor Cards (Category B - Draft vs Official Release)
    const val REPORT_CARD_READ = "exam:report-card:read"
    const val REPORT_CARD_GENERATE = "exam:report-card:generate"
    const val REPORT_CARD_PUBLISH = "exam:report-card:publish"

    // =========================================================================
    // Finance, Fees & Invoicing Context (finance:*)
    // =========================================================================
    // Fee Catalog & Structures (Category A - Master CRUD)
    const val FEE_STRUCTURE_READ = "finance:fee:read"
    const val FEE_STRUCTURE_CREATE = "finance:fee:create"
    const val FEE_STRUCTURE_UPDATE = "finance:fee:update"
    const val FEE_STRUCTURE_DELETE = "finance:fee:delete"
    const val FEE_STRUCTURE_MANAGE = "finance:fee:manage" // Legacy alias

    // Invoices & Billing
    const val INVOICE_READ = "finance:invoice:read"
    const val INVOICE_CREATE = "finance:invoice:create"

    // Payments & Receipts (Category B - Maker-Checker Reversals)
    const val PAYMENT_READ = "finance:payment:read"
    const val PAYMENT_RECORD = "finance:payment:create"
    const val PAYMENT_VOID = "finance:payment:void"

    // Financial Reports & Arrears
    const val FINANCIAL_REPORT_READ = "finance:report:read"

    // =========================================================================
    // Human Resources & Payroll Context (hr:*)
    // =========================================================================
    // Employee Master Registry (Category A - Master CRUD)
    const val STAFF_PROFILE_READ = "hr:staff:read"
    const val STAFF_PROFILE_CREATE = "hr:staff:create"
    const val STAFF_PROFILE_UPDATE = "hr:staff:update"
    const val STAFF_PROFILE_DELETE = "hr:staff:delete"
    const val STAFF_PROFILE_WRITE = "hr:staff:write" // Legacy alias

    // Teaching & Homeroom Allocation (Category D - Relational Mapping)
    const val TEACHER_ASSIGNMENT_READ = "hr:assignment:read"
    const val TEACHER_ASSIGNMENT_MANAGE = "hr:assignment:manage"

    // Staff Leave Workflow (Category B - Maker-Checker)
    const val STAFF_LEAVE_REQUEST = "hr:leave:request"
    const val STAFF_LEAVE_APPROVE = "hr:leave:approve"

    // Monthly Payroll Processing (Category C - Lifecycle)
    const val PAYROLL_READ = "hr:payroll:read"
    const val PAYROLL_CALCULATE = "hr:payroll:calculate"
    const val PAYROLL_DISBURSE = "hr:payroll:disburse"

    // =========================================================================
    // Audit Trail Context (audit:*)
    // =========================================================================
    const val AUDIT_LOG_READ = "audit:log:read"
}
