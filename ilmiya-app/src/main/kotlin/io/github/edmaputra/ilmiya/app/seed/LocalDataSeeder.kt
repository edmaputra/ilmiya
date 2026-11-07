package io.github.edmaputra.ilmiya.app.seed

import io.github.edmaputra.iam.application.port.`in`.ManageRoleUseCase
import io.github.edmaputra.iam.application.port.`in`.ManageUserUseCase
import io.github.edmaputra.iam.application.port.`in`.RoleCommands
import io.github.edmaputra.iam.application.port.`in`.UserCommands
import io.github.edmaputra.iam.domain.repository.RoleRepository
import io.github.edmaputra.iam.domain.repository.UserRepository
import io.github.edmaputra.iam.domain.tenancy.TenantId as IamTenantId
import io.github.edmaputra.ilmiya.academic.domain.AcademicTermType
import io.github.edmaputra.ilmiya.academic.domain.port.`in`.ActivateAcademicTermUseCase
import io.github.edmaputra.ilmiya.academic.domain.port.`in`.ActivateAcademicYearUseCase
import io.github.edmaputra.ilmiya.academic.domain.port.`in`.AddAcademicTermCommand
import io.github.edmaputra.ilmiya.academic.domain.port.`in`.AddAcademicTermUseCase
import io.github.edmaputra.ilmiya.academic.domain.port.`in`.CreateAcademicYearCommand
import io.github.edmaputra.ilmiya.academic.domain.port.`in`.CreateAcademicYearUseCase
import io.github.edmaputra.ilmiya.common.context.TenantContext
import io.github.edmaputra.ilmiya.common.domain.TenantId
import io.github.edmaputra.ilmiya.core.school.domain.Accreditation
import io.github.edmaputra.ilmiya.core.school.domain.GradingPreference
import io.github.edmaputra.ilmiya.core.school.domain.OperationalSettings
import io.github.edmaputra.ilmiya.core.school.domain.TermLayout
import io.github.edmaputra.ilmiya.core.school.domain.port.`in`.RegisterSchoolCommand
import io.github.edmaputra.ilmiya.core.school.domain.port.`in`.RegisterSchoolUseCase
import io.github.edmaputra.ilmiya.core.school.domain.port.out.SchoolRepository
import io.github.edmaputra.ilmiya.core.security.IlmiyaPermissionCatalog
import io.github.edmaputra.ilmiya.core.security.IlmiyaPermissions
import io.github.edmaputra.ilmiya.core.security.IlmiyaRoles
import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.time.DayOfWeek
import java.time.LocalDate

/**
 * Local environment data seeder that initializes baseline development entities
 * (School, Operational Settings, Academic Year, Terms) and IAM seed data (Roles, Users, Role Assignments).
 *
 * Runs exclusively when the 'local' profile is active and 'ilmiya.seed.enabled' is true.
 *
 * @author edmaputra
 * @since 0.0.1
 */
@Component
@Profile("local")
@ConditionalOnProperty(name = ["ilmiya.seed.enabled"], havingValue = "true", matchIfMissing = true)
class LocalDataSeeder(
    private val schoolRepository: SchoolRepository,
    private val registerSchoolUseCase: RegisterSchoolUseCase,
    private val createAcademicYearUseCase: CreateAcademicYearUseCase,
    private val activateAcademicYearUseCase: ActivateAcademicYearUseCase,
    private val addAcademicTermUseCase: AddAcademicTermUseCase,
    private val activateAcademicTermUseCase: ActivateAcademicTermUseCase,
    private val manageUserUseCase: ManageUserUseCase,
    private val manageRoleUseCase: ManageRoleUseCase,
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository
) : ApplicationRunner {

    private val log = LoggerFactory.getLogger(javaClass)

    companion object {
        const val SAMPLE_TENANT_ID = "01940000-0000-7000-8000-000000000001"
        const val ADMIN_EMAIL = "admin@springfield.local"
        const val ADMIN_PASSWORD = "Password123!"
        const val SUPERADMIN_EMAIL = "superadmin@ilmiya.local"
        const val SUPERADMIN_PASSWORD = "Password123!"
    }

    override fun run(args: ApplicationArguments) {
        val tenantId = TenantId.of(SAMPLE_TENANT_ID)
        val iamTenantId = IamTenantId.from(SAMPLE_TENANT_ID)

        seedSchoolDomainData(tenantId)
        seedIamData(iamTenantId)
    }

    private fun seedSchoolDomainData(tenantId: TenantId) {
        if (schoolRepository.existsByTenantId(tenantId)) {
            log.info("School domain seed data for tenant '{}' already exists. Skipping.", tenantId.value)
            return
        }

        log.info("Starting local school domain data seeding for tenant '{}'...", tenantId.value)

        // 1. Register School
        val school = registerSchoolUseCase.register(
            RegisterSchoolCommand(
                tenantId = tenantId.value,
                name = "Springfield Green Academy",
                institutionCode = "SGA-001",
                governmentRegistrationNumber = "NPSN-10020030",
                registrationType = "NPSN",
                address = "742 Evergreen Terrace, Springfield",
                contactEmail = ADMIN_EMAIL,
                contactPhone = "+1-555-0199",
                accreditation = Accreditation.UNACCREDITED,
                operationalSettings = OperationalSettings(
                    workingDays = setOf(
                        DayOfWeek.MONDAY,
                        DayOfWeek.TUESDAY,
                        DayOfWeek.WEDNESDAY,
                        DayOfWeek.THURSDAY,
                        DayOfWeek.FRIDAY
                    ),
                    gradingPreference = GradingPreference.NUMERIC_0_100,
                    termLayout = TermLayout.SEMESTER
                )
            )
        )
        log.info("Registered seed school: '{}' (code: '{}')", school.name, school.institutionCode)

        // 2. Provision Academic Year & Terms scoped to the Tenant
        TenantContext.runWithTenant(tenantId) {
            val academicYear = createAcademicYearUseCase.create(
                tenantId,
                CreateAcademicYearCommand(
                    name = "2026/2027",
                    startDate = LocalDate.of(2026, 7, 1),
                    endDate = LocalDate.of(2027, 6, 30)
                )
            )
            activateAcademicYearUseCase.activate(tenantId, academicYear.id)
            log.info("Created and activated academic year '{}' (ID: {})", academicYear.name, academicYear.id.value)

            val term1 = addAcademicTermUseCase.addTerm(
                tenantId,
                academicYear.id,
                AddAcademicTermCommand(
                    name = "Semester 1",
                    type = AcademicTermType.SEMESTER_1,
                    displayName = "Odd Semester 2026/2027",
                    startDate = LocalDate.of(2026, 7, 1),
                    endDate = LocalDate.of(2026, 12, 31)
                )
            )
            activateAcademicTermUseCase.activateTerm(tenantId, academicYear.id, term1.id)
            log.info("Added and activated term '{}' (ID: {})", term1.displayName, term1.id.value)

            val term2 = addAcademicTermUseCase.addTerm(
                tenantId,
                academicYear.id,
                AddAcademicTermCommand(
                    name = "Semester 2",
                    type = AcademicTermType.SEMESTER_2,
                    displayName = "Even Semester 2026/2027",
                    startDate = LocalDate.of(2027, 1, 1),
                    endDate = LocalDate.of(2027, 6, 30)
                )
            )
            log.info("Added planned term '{}' (ID: {})", term2.displayName, term2.id.value)

            log.info("School domain data seeding completed successfully for tenant '{}'.", tenantId.value)
        }
    }

    private fun seedIamData(iamTenantId: IamTenantId) {
        log.info("Checking local IAM seed data...")

        val schoolAdminPermissions = IlmiyaPermissionCatalog.ENTRIES.map { it.code }.toSet()

        // 1. Seed School Admin Role and User
        if (!userRepository.existsByEmail(ADMIN_EMAIL)) {
            val role = if (!roleRepository.existsByTenantIdAndCode(iamTenantId, IlmiyaRoles.SCHOOL_ADMIN)) {
                manageRoleUseCase.createRole(
                    RoleCommands.CreateRoleCommand(
                        iamTenantId,
                        IlmiyaRoles.SCHOOL_ADMIN,
                        "School Administrator",
                        "Full administrative access for the school tenant",
                        schoolAdminPermissions
                    )
                )
            } else {
                roleRepository.findByTenantIdAndCode(iamTenantId, IlmiyaRoles.SCHOOL_ADMIN).orElseThrow()
            }

            val adminUser = manageUserUseCase.createUser(
                UserCommands.CreateUserCommand(
                    ADMIN_EMAIL,
                    ADMIN_PASSWORD,
                    "Springfield Admin",
                    false
                )
            )

            manageUserUseCase.assignRole(
                UserCommands.AssignUserRoleCommand(
                    adminUser.id,
                    role.id,
                    iamTenantId,
                    null
                )
            )
            log.info("Provisioned local IAM admin user '{}' with role '{}'", ADMIN_EMAIL, role.code)
        } else {
            log.info("Local IAM admin user '{}' already exists. Skipping.", ADMIN_EMAIL)
        }

        // 2. Seed Platform Super Admin User
        if (!userRepository.existsByEmail(SUPERADMIN_EMAIL)) {
            manageUserUseCase.createUser(
                UserCommands.CreateUserCommand(
                    SUPERADMIN_EMAIL,
                    SUPERADMIN_PASSWORD,
                    "Platform Super Admin",
                    true
                )
            )
            log.info("Provisioned local IAM platform superadmin user '{}'", SUPERADMIN_EMAIL)
        } else {
            log.info("Local IAM platform superadmin user '{}' already exists. Skipping.", SUPERADMIN_EMAIL)
        }
    }
}
