package io.github.edmaputra.ilmiya.core.security

/**
 * Standard Role definitions in Ilmiya across Tenant, School, and Self-Service scopes.
 *
 * Corresponds to the official specification defined in `docs/02-roles-and-permissions.md`.
 *
 * @author edmaputra
 * @since 0.0.1
 */
object IlmiyaRoles {

    // Tier 1: Platform Administration
    const val SUPERADMIN = "SUPERADMIN"

    // Tier 2: Foundation & Organization Governance (Tenant Scope)
    const val TENANT_ADMIN = "TENANT_ADMIN"
    const val FOUNDATION_BOARD = "FOUNDATION_BOARD"

    // Tier 3: School Leadership & Governance (School Scope)
    const val SCHOOL_ADMIN = "SCHOOL_ADMIN"
    const val PRINCIPAL = "PRINCIPAL"
    const val VICE_PRINCIPAL_ACADEMIC = "VICE_PRINCIPAL_ACADEMIC"
    const val VICE_PRINCIPAL_STUDENTS = "VICE_PRINCIPAL_STUDENTS"

    // Tier 4: Academic, Operational & Administrative Staff (School Scope)
    const val ACADEMIC_ADMIN = "ACADEMIC_ADMIN"
    const val TEACHER = "TEACHER"
    const val HOMEROOM_TEACHER = "HOMEROOM_TEACHER"
    const val BURSAR = "BURSAR"
    const val HR_OFFICER = "HR_OFFICER"
    const val DORMITORY_WARDEN = "DORMITORY_WARDEN"

    // Tier 5: Self-Service End-Users
    const val STUDENT = "STUDENT"
    const val PARENT = "PARENT"

    /**
     * Unmodifiable set of all standard Ilmiya roles.
     */
    val ALL_ROLES: Set<String> = setOf(
        SUPERADMIN,
        TENANT_ADMIN,
        FOUNDATION_BOARD,
        SCHOOL_ADMIN,
        PRINCIPAL,
        VICE_PRINCIPAL_ACADEMIC,
        VICE_PRINCIPAL_STUDENTS,
        ACADEMIC_ADMIN,
        TEACHER,
        HOMEROOM_TEACHER,
        BURSAR,
        HR_OFFICER,
        DORMITORY_WARDEN,
        STUDENT,
        PARENT
    )
}
