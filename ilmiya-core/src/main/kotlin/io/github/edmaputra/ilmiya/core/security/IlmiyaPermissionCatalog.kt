package io.github.edmaputra.ilmiya.core.security

/**
 * Descriptor record representing a permission entry in the catalog.
 *
 * @property code The unique permission code (e.g. `school:profile:read`, `tenant:*`)
 * @property name Human-readable display name
 * @property description Detailed explanation of the entitlement
 * @property category Functional grouping (e.g. `TENANT`, `SCHOOL`, `ACADEMIC`, `STUDENT`)
 *
 * @author edmaputra
 * @since 0.1.0
 */
data class PermissionCatalogEntry(
    val code: String,
    val name: String,
    val description: String,
    val category: String
)

/**
 * Comprehensive catalog of all standard system permissions defined within the Ilmiya platform.
 *
 * Adheres strictly to `{domain}:{resource}:{action}` syntax conforming to the
 * Hybrid Authorization Model and enabling hierarchical wildcard resolution.
 *
 * @author edmaputra
 * @since 0.1.0
 */
object IlmiyaPermissionCatalog {

    val ENTRIES: List<PermissionCatalogEntry> = listOf(
        // =========================================================================
        // Tenant & Foundation Context (tenant:*)
        // =========================================================================
        PermissionCatalogEntry(
            code = IlmiyaPermissions.TENANT_READ,
            name = "Read Tenant Management",
            description = "Allows viewing tenant organizations, limits, and operational statuses",
            category = "TENANT"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.TENANT_CREATE,
            name = "Create Tenant",
            description = "Allows provisioning new school tenants and educational organizations",
            category = "TENANT"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.TENANT_UPDATE,
            name = "Update Tenant",
            description = "Allows modifying tenant details, tier limits, and administrative settings",
            category = "TENANT"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.TENANT_DELETE,
            name = "Delete Tenant",
            description = "Allows deactivating or deleting school tenant accounts",
            category = "TENANT"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.TENANT_PROFILE_READ,
            name = "Read Tenant Profile",
            description = "Allows viewing tenant organization profile and institutional metadata",
            category = "TENANT"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.TENANT_PROFILE_WRITE,
            name = "Update Tenant Profile",
            description = "Allows modifying tenant organization profile and contact information",
            category = "TENANT"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.TENANT_SCHOOL_MANAGE,
            name = "Manage Tenant Schools",
            description = "Allows orchestrating and assigning school entities within a tenant organization",
            category = "TENANT"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.TENANT_SUBSCRIPTION_READ,
            name = "Read Tenant Subscription",
            description = "Allows inspecting tenant subscription plans, quotas, and feature entitlements",
            category = "TENANT"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.TENANT_AUDIT_READ,
            name = "Read Tenant Audit",
            description = "Allows reviewing organization-wide operational and security audit trails",
            category = "TENANT"
        ),
        PermissionCatalogEntry(
            code = "tenant:*",
            name = "Tenant Domain Wildcard",
            description = "Full administrative access across all tenant operations and configuration",
            category = "TENANT"
        ),

        // =========================================================================
        // School Profile, Settings & Users Context (school:*)
        // =========================================================================
        PermissionCatalogEntry(
            code = IlmiyaPermissions.SCHOOL_REGISTER,
            name = "Register School",
            description = "Allows registering a new educational institution profile",
            category = "SCHOOL"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.SCHOOL_PROFILE_READ,
            name = "Read School Profile",
            description = "Allows viewing school institutional profile, identity, and address",
            category = "SCHOOL"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.SCHOOL_PROFILE_UPDATE,
            name = "Update School Profile",
            description = "Allows modifying school institutional information, contact details, and accreditation",
            category = "SCHOOL"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.SCHOOL_SETTINGS_READ,
            name = "Read School Settings",
            description = "Allows inspecting school operational schedule, term layout, and grading preferences",
            category = "SCHOOL"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.SCHOOL_SETTINGS_MANAGE,
            name = "Manage School Settings",
            description = "Allows configuring school operational hours, term layout, and grading scales",
            category = "SCHOOL"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.SCHOOL_USER_READ,
            name = "Read School Users",
            description = "Allows viewing staff and administrative user accounts assigned to the school",
            category = "SCHOOL"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.SCHOOL_USER_CREATE,
            name = "Create School User",
            description = "Allows provisioning school administrator and staff user accounts",
            category = "SCHOOL"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.SCHOOL_USER_UPDATE,
            name = "Update School User",
            description = "Allows modifying school user credentials, status, and role assignments",
            category = "SCHOOL"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.SCHOOL_USER_DELETE,
            name = "Delete School User",
            description = "Allows suspending or deactivating school user accounts",
            category = "SCHOOL"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.SCHOOL_AUDIT_READ,
            name = "Read School Audit",
            description = "Allows inspecting school-level administrative audit events",
            category = "SCHOOL"
        ),
        PermissionCatalogEntry(
            code = "school:*",
            name = "School Domain Wildcard",
            description = "Full administrative access across all school profiles, operational settings, and users",
            category = "SCHOOL"
        ),

        // =========================================================================
        // Academic Structure & Curriculum Context (academic:*)
        // =========================================================================
        PermissionCatalogEntry(
            code = IlmiyaPermissions.ACADEMIC_YEAR_READ,
            name = "Read Academic Year",
            description = "Allows viewing academic calendar years and their active status",
            category = "ACADEMIC"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.ACADEMIC_YEAR_CREATE,
            name = "Create Academic Year",
            description = "Allows establishing a new academic year in the school calendar",
            category = "ACADEMIC"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.ACADEMIC_YEAR_UPDATE,
            name = "Update Academic Year",
            description = "Allows updating academic year dates and activating/closing academic years",
            category = "ACADEMIC"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.ACADEMIC_YEAR_DELETE,
            name = "Delete Academic Year",
            description = "Allows deleting planned or unactivated academic years",
            category = "ACADEMIC"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.ACADEMIC_TERM_READ,
            name = "Read Academic Term",
            description = "Allows viewing academic terms, semesters, and quarter schedules",
            category = "ACADEMIC"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.ACADEMIC_TERM_CREATE,
            name = "Create Academic Term",
            description = "Allows defining terms and semesters within an academic year",
            category = "ACADEMIC"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.ACADEMIC_TERM_UPDATE,
            name = "Update Academic Term",
            description = "Allows modifying term date ranges and setting active term status",
            category = "ACADEMIC"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.ACADEMIC_TERM_DELETE,
            name = "Delete Academic Term",
            description = "Allows removing planned or uncommenced academic terms",
            category = "ACADEMIC"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.CURRICULUM_READ,
            name = "Read Curriculum",
            description = "Allows inspecting curriculum frameworks, grade level structures, and syllabi",
            category = "ACADEMIC"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.CURRICULUM_MANAGE,
            name = "Manage Curriculum",
            description = "Allows creating and modifying curriculum standards and learning outcomes",
            category = "ACADEMIC"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.COURSE_READ,
            name = "Read Course Catalog",
            description = "Allows viewing the master subject course catalog and syllabus details",
            category = "ACADEMIC"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.COURSE_CREATE,
            name = "Create Course",
            description = "Allows registering new subject courses in the master course catalog",
            category = "ACADEMIC"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.COURSE_UPDATE,
            name = "Update Course",
            description = "Allows updating course codes, credit hours, descriptions, and syllabi",
            category = "ACADEMIC"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.COURSE_DELETE,
            name = "Delete Course",
            description = "Allows archiving or removing inactive course offerings",
            category = "ACADEMIC"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.CLASS_READ,
            name = "Read Class Sections",
            description = "Allows viewing classrooms, student sections, and grade level rosters",
            category = "ACADEMIC"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.CLASS_CREATE,
            name = "Create Class Section",
            description = "Allows setting up new classroom sections and student cohorts",
            category = "ACADEMIC"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.CLASS_UPDATE,
            name = "Update Class Section",
            description = "Allows updating class capacities, homerooms, and classroom assignments",
            category = "ACADEMIC"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.CLASS_DELETE,
            name = "Delete Class Section",
            description = "Allows removing empty or unallocated class sections",
            category = "ACADEMIC"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.PROMOTION_READ,
            name = "Read Promotion Workflow",
            description = "Allows viewing year-end student promotion criteria and candidate lists",
            category = "ACADEMIC"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.PROMOTION_EXECUTE,
            name = "Execute Promotion Workflow",
            description = "Allows executing batch year-end student class promotion and graduation",
            category = "ACADEMIC"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.PROMOTION_ROLLBACK,
            name = "Rollback Promotion Workflow",
            description = "Allows rolling back an erroneous batch promotion operation",
            category = "ACADEMIC"
        ),
        PermissionCatalogEntry(
            code = "academic:*",
            name = "Academic Domain Wildcard",
            description = "Full administrative access across all academic years, terms, courses, and classes",
            category = "ACADEMIC"
        ),

        // =========================================================================
        // Student Lifecycle & Admissions Context (student:*)
        // =========================================================================
        PermissionCatalogEntry(
            code = IlmiyaPermissions.STUDENT_PROFILE_READ,
            name = "Read Student Profile",
            description = "Allows viewing student biographical, demographic, and academic records",
            category = "STUDENT"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.STUDENT_PROFILE_CREATE,
            name = "Create Student Profile",
            description = "Allows registering and admitting new student profiles into the institution",
            category = "STUDENT"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.STUDENT_PROFILE_UPDATE,
            name = "Update Student Profile",
            description = "Allows modifying student contact details, addresses, and emergency information",
            category = "STUDENT"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.STUDENT_PROFILE_DELETE,
            name = "Delete Student Profile",
            description = "Allows archiving, deactivating, or expunging student records",
            category = "STUDENT"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.STUDENT_ADMISSION_READ,
            name = "Read Admissions",
            description = "Allows reviewing prospective student admission applications and intake pipeline",
            category = "STUDENT"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.STUDENT_ADMISSION_APPLY,
            name = "Submit Admission Application",
            description = "Allows submitting new applicant admission forms (PPDB)",
            category = "STUDENT"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.STUDENT_ADMISSION_REVIEW,
            name = "Review Admission Application",
            description = "Allows evaluating candidate qualifications, entrance tests, and interviews",
            category = "STUDENT"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.STUDENT_ADMISSION_APPROVE,
            name = "Approve Admission Application",
            description = "Allows formally accepting admission candidates and issuing matriculation offers",
            category = "STUDENT"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.STUDENT_ENROLLMENT_MANAGE,
            name = "Manage Class Enrollment",
            description = "Allows assigning, transferring, and withdrawing students from class sections",
            category = "STUDENT"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.GUARDIAN_LINK_MANAGE,
            name = "Manage Guardian Linkage",
            description = "Allows establishing and verifying legal parent and guardian associations",
            category = "STUDENT"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.STUDENT_DOCUMENT_READ,
            name = "Read Student Documents",
            description = "Allows inspecting uploaded compliance certificates, transcripts, and records",
            category = "STUDENT"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.STUDENT_DOCUMENT_UPLOAD,
            name = "Upload Student Document",
            description = "Allows attaching birth certificates, medical forms, and transcripts to students",
            category = "STUDENT"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.STUDENT_DOCUMENT_DELETE,
            name = "Delete Student Document",
            description = "Allows removing superseded or rejected student documents",
            category = "STUDENT"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.STUDENT_CONDUCT_READ,
            name = "Read Student Conduct",
            description = "Allows viewing student behavioral logs, achievements, and counseling notes",
            category = "STUDENT"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.STUDENT_CONDUCT_RECORD,
            name = "Record Student Conduct",
            description = "Allows logging disciplinary incidents, counseling notes, and merit awards",
            category = "STUDENT"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.STUDENT_CONDUCT_UPDATE,
            name = "Update Student Conduct",
            description = "Allows modifying conduct incident logs and updating resolution statuses",
            category = "STUDENT"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.STUDENT_CONDUCT_DELETE,
            name = "Delete Student Conduct",
            description = "Allows expunging erroneous student conduct records",
            category = "STUDENT"
        ),
        PermissionCatalogEntry(
            code = "student:*",
            name = "Student Domain Wildcard",
            description = "Full administrative access across all student profiles, admissions, documents, and conduct",
            category = "STUDENT"
        ),

        // =========================================================================
        // Attendance Tracking Context (attendance:*)
        // =========================================================================
        PermissionCatalogEntry(
            code = IlmiyaPermissions.ATTENDANCE_READ,
            name = "Read Attendance Records",
            description = "Allows viewing classroom, session, and campus attendance summaries",
            category = "ATTENDANCE"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.ATTENDANCE_RECORD,
            name = "Record Attendance",
            description = "Allows taking and submitting classroom daily and period attendance",
            category = "ATTENDANCE"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.ATTENDANCE_MODIFY,
            name = "Modify Attendance Record",
            description = "Allows correcting submitted attendance statuses, timestamps, and notes",
            category = "ATTENDANCE"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.ATTENDANCE_APPROVE,
            name = "Approve Attendance Submissions",
            description = "Allows supervisor sign-off and verification of daily attendance submissions",
            category = "ATTENDANCE"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.ATTENDANCE_STUDENT_READ,
            name = "Read Student Attendance History",
            description = "Allows inspecting individual student presence, absence, and tardiness history",
            category = "ATTENDANCE"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.ATTENDANCE_STUDENT_RECORD,
            name = "Record Student Check-in",
            description = "Allows logging student gate entries, biometric scans, and badge check-ins",
            category = "ATTENDANCE"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.ATTENDANCE_STUDENT_UPDATE,
            name = "Update Student Attendance Status",
            description = "Allows updating student absence classifications (excused, unexcused, medical)",
            category = "ATTENDANCE"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.ATTENDANCE_STUDENT_EXCUSE,
            name = "Approve Attendance Excuse",
            description = "Allows reviewing and approving medical certificates and parent absence letters",
            category = "ATTENDANCE"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.ATTENDANCE_STAFF_READ,
            name = "Read Staff Attendance",
            description = "Allows viewing faculty and staff work presence and shift timesheets",
            category = "ATTENDANCE"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.ATTENDANCE_STAFF_RECORD,
            name = "Record Staff Attendance",
            description = "Allows logging staff work arrival, departures, and duty check-ins",
            category = "ATTENDANCE"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.ATTENDANCE_STAFF_APPROVE,
            name = "Approve Staff Timesheets",
            description = "Allows administrative approval of staff monthly attendance and timesheets",
            category = "ATTENDANCE"
        ),
        PermissionCatalogEntry(
            code = "attendance:*",
            name = "Attendance Domain Wildcard",
            description = "Full administrative access across all student and staff attendance tracking",
            category = "ATTENDANCE"
        ),

        // =========================================================================
        // Timetable & Calendar Context (timetable:*)
        // =========================================================================
        PermissionCatalogEntry(
            code = IlmiyaPermissions.TIMETABLE_READ,
            name = "Read Timetable",
            description = "Allows viewing class, educator, and room schedule timetables",
            category = "TIMETABLE"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.TIMETABLE_SCHEDULE_CREATE,
            name = "Create Timetable Schedule",
            description = "Allows scheduling class periods, teacher allocations, and lab slots",
            category = "TIMETABLE"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.TIMETABLE_SCHEDULE_UPDATE,
            name = "Update Timetable Schedule",
            description = "Allows adjusting period timings, educator substitutions, and room reassignments",
            category = "TIMETABLE"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.TIMETABLE_SCHEDULE_DELETE,
            name = "Delete Timetable Schedule",
            description = "Allows clearing or deleting draft timetable allocations",
            category = "TIMETABLE"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.TIMETABLE_SCHEDULE_PUBLISH,
            name = "Publish Master Timetable",
            description = "Allows releasing the finalized timetable to teachers, students, and parents",
            category = "TIMETABLE"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.TIMETABLE_CALENDAR_READ,
            name = "Read School Calendar",
            description = "Allows viewing institutional milestones, holidays, and semester events",
            category = "TIMETABLE"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.TIMETABLE_CALENDAR_MANAGE,
            name = "Manage School Calendar",
            description = "Allows creating and managing institutional holidays, exam weeks, and special events",
            category = "TIMETABLE"
        ),
        PermissionCatalogEntry(
            code = "timetable:*",
            name = "Timetable Domain Wildcard",
            description = "Full administrative access across all timetables, class schedules, and school calendars",
            category = "TIMETABLE"
        ),

        // =========================================================================
        // Examination, Assessment & e-Rapor Context (exam:*)
        // =========================================================================
        PermissionCatalogEntry(
            code = IlmiyaPermissions.EXAM_SCHEDULE_READ,
            name = "Read Exam Schedule",
            description = "Allows viewing examination dates, proctor duties, and student seatings",
            category = "EXAM"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.EXAM_SCHEDULE_CREATE,
            name = "Create Exam Schedule",
            description = "Allows establishing mid-term and final examination sessions and room rosters",
            category = "EXAM"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.EXAM_SCHEDULE_UPDATE,
            name = "Update Exam Schedule",
            description = "Allows modifying examination timings, room rosters, and proctor assignments",
            category = "EXAM"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.EXAM_SCHEDULE_DELETE,
            name = "Delete Exam Schedule",
            description = "Allows canceling planned exam sessions and resetting schedules",
            category = "EXAM"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.GRADE_READ,
            name = "Read Grades",
            description = "Allows viewing formative assessments, homework scores, and examination marks",
            category = "EXAM"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.GRADE_ENTRY,
            name = "Enter Grades",
            description = "Allows teachers to enter assessment scores, weights, and rubric marks",
            category = "EXAM"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.GRADE_MODIFY,
            name = "Modify Grades",
            description = "Allows correcting entered student scores before grade closure",
            category = "EXAM"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.GRADE_LOCK,
            name = "Lock Grades",
            description = "Allows dual-control freezing of gradebooks to prevent post-deadline modifications",
            category = "EXAM"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.REPORT_CARD_READ,
            name = "Read Report Cards",
            description = "Allows viewing student term performance summaries and e-Rapor cards",
            category = "EXAM"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.REPORT_CARD_GENERATE,
            name = "Generate Report Cards",
            description = "Allows calculating final weighted marks and generating official e-Rapor records",
            category = "EXAM"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.REPORT_CARD_PUBLISH,
            name = "Publish Report Cards",
            description = "Allows official release of report cards to parents and student portals",
            category = "EXAM"
        ),
        PermissionCatalogEntry(
            code = "exam:*",
            name = "Exam Domain Wildcard",
            description = "Full administrative access across all examinations, grades, and report card operations",
            category = "EXAM"
        ),

        // =========================================================================
        // Finance, Fees & Invoicing Context (finance:*)
        // =========================================================================
        PermissionCatalogEntry(
            code = IlmiyaPermissions.FEE_STRUCTURE_READ,
            name = "Read Fee Structures",
            description = "Allows viewing tuition fee schedules, installment options, and scholarship criteria",
            category = "FINANCE"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.FEE_STRUCTURE_CREATE,
            name = "Create Fee Structure",
            description = "Allows setting up tuition rates, admission fees, and extracurricular charges",
            category = "FINANCE"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.FEE_STRUCTURE_UPDATE,
            name = "Update Fee Structure",
            description = "Allows updating fee amounts, due date rules, and sibling discounts",
            category = "FINANCE"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.FEE_STRUCTURE_DELETE,
            name = "Delete Fee Structure",
            description = "Allows removing unbilled or obsolete fee definitions",
            category = "FINANCE"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.INVOICE_READ,
            name = "Read Invoices",
            description = "Allows inspecting student tuition invoices, billing history, and balances",
            category = "FINANCE"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.INVOICE_CREATE,
            name = "Create Invoices",
            description = "Allows generating individual and batch monthly tuition invoices",
            category = "FINANCE"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.PAYMENT_READ,
            name = "Read Payments",
            description = "Allows viewing payment transactions, receipts, and bank reconciliation entries",
            category = "FINANCE"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.PAYMENT_RECORD,
            name = "Record Payment",
            description = "Allows logging received cash, virtual account, and credit card payments",
            category = "FINANCE"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.PAYMENT_VOID,
            name = "Void Payment",
            description = "Allows dual-control voiding or reversal of erroneous payment records",
            category = "FINANCE"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.FINANCIAL_REPORT_READ,
            name = "Read Financial Reports",
            description = "Allows reviewing tuition collection summaries, arrears, and revenue reports",
            category = "FINANCE"
        ),
        PermissionCatalogEntry(
            code = "finance:*",
            name = "Finance Domain Wildcard",
            description = "Full administrative access across all fees, invoices, payments, and financial reports",
            category = "FINANCE"
        ),

        // =========================================================================
        // Human Resources & Payroll Context (hr:*)
        // =========================================================================
        PermissionCatalogEntry(
            code = IlmiyaPermissions.STAFF_PROFILE_READ,
            name = "Read Staff Profile",
            description = "Allows viewing employee information, contracts, and employment status",
            category = "HR"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.STAFF_PROFILE_CREATE,
            name = "Create Staff Profile",
            description = "Allows onboarding and creating new employee and educator records",
            category = "HR"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.STAFF_PROFILE_UPDATE,
            name = "Update Staff Profile",
            description = "Allows modifying staff contact details, salary grades, and certifications",
            category = "HR"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.STAFF_PROFILE_DELETE,
            name = "Delete Staff Profile",
            description = "Allows terminating or archiving employee records",
            category = "HR"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.TEACHER_ASSIGNMENT_READ,
            name = "Read Teacher Assignments",
            description = "Allows inspecting teaching workloads and assigned classroom subjects",
            category = "HR"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.TEACHER_ASSIGNMENT_MANAGE,
            name = "Manage Teacher Assignments",
            description = "Allows allocating educators to specific subjects, grade levels, and homerooms",
            category = "HR"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.STAFF_LEAVE_REQUEST,
            name = "Submit Leave Request",
            description = "Allows staff members to apply for annual, sick, or personal leave",
            category = "HR"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.STAFF_LEAVE_APPROVE,
            name = "Approve Leave Request",
            description = "Allows department heads and principals to authorize employee leave requests",
            category = "HR"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.PAYROLL_READ,
            name = "Read Payroll",
            description = "Allows viewing staff salary computations, deductions, and payslips",
            category = "HR"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.PAYROLL_CALCULATE,
            name = "Calculate Payroll",
            description = "Allows executing monthly employee salary, tax, and deduction calculations",
            category = "HR"
        ),
        PermissionCatalogEntry(
            code = IlmiyaPermissions.PAYROLL_DISBURSE,
            name = "Disburse Payroll",
            description = "Allows authorizing and releasing monthly payroll disbursements",
            category = "HR"
        ),
        PermissionCatalogEntry(
            code = "hr:*",
            name = "HR Domain Wildcard",
            description = "Full administrative access across all staff, educator assignments, leave, and payroll",
            category = "HR"
        ),

        // =========================================================================
        // Audit Trail Context (audit:*)
        // =========================================================================
        PermissionCatalogEntry(
            code = IlmiyaPermissions.AUDIT_LOG_READ,
            name = "Read Platform Audit Logs",
            description = "Allows inspecting platform-wide administrative and security audit trail logs",
            category = "AUDIT"
        )
    )

    /**
     * Map of permissions indexed by unique permission code for fast lookups.
     */
    val BY_CODE: Map<String, PermissionCatalogEntry> = ENTRIES.associateBy { it.code }

    /**
     * List of unique permission categories defined in the catalog.
     */
    val CATEGORIES: Set<String> = ENTRIES.map { it.category }.toSet()
}
