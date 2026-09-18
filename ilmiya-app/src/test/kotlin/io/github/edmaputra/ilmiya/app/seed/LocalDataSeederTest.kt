package io.github.edmaputra.ilmiya.app.seed

import io.github.edmaputra.iam.application.port.`in`.ManageRoleUseCase
import io.github.edmaputra.iam.application.port.`in`.ManageUserUseCase
import io.github.edmaputra.iam.application.port.`in`.RoleCommands
import io.github.edmaputra.iam.application.port.`in`.UserCommands
import io.github.edmaputra.iam.domain.model.Role
import io.github.edmaputra.iam.domain.model.RoleId
import io.github.edmaputra.iam.domain.model.User
import io.github.edmaputra.iam.domain.model.UserId
import io.github.edmaputra.iam.domain.model.UserRoleAssignment
import io.github.edmaputra.iam.domain.model.UserStatus
import io.github.edmaputra.iam.domain.repository.RoleRepository
import io.github.edmaputra.iam.domain.repository.UserRepository
import io.github.edmaputra.iam.domain.tenancy.TenantId as IamTenantId
import io.github.edmaputra.ilmiya.academic.domain.AcademicTerm
import io.github.edmaputra.ilmiya.academic.domain.AcademicTermId
import io.github.edmaputra.ilmiya.academic.domain.AcademicTermType
import io.github.edmaputra.ilmiya.academic.domain.AcademicYear
import io.github.edmaputra.ilmiya.academic.domain.AcademicYearId
import io.github.edmaputra.ilmiya.academic.domain.port.`in`.ActivateAcademicTermUseCase
import io.github.edmaputra.ilmiya.academic.domain.port.`in`.ActivateAcademicYearUseCase
import io.github.edmaputra.ilmiya.academic.domain.port.`in`.AddAcademicTermCommand
import io.github.edmaputra.ilmiya.academic.domain.port.`in`.AddAcademicTermUseCase
import io.github.edmaputra.ilmiya.academic.domain.port.`in`.CreateAcademicYearCommand
import io.github.edmaputra.ilmiya.academic.domain.port.`in`.CreateAcademicYearUseCase
import io.github.edmaputra.ilmiya.common.domain.TenantId
import io.github.edmaputra.ilmiya.core.school.domain.School
import io.github.edmaputra.ilmiya.core.school.domain.SchoolId
import io.github.edmaputra.ilmiya.core.school.domain.port.`in`.RegisterSchoolCommand
import io.github.edmaputra.ilmiya.core.school.domain.port.`in`.RegisterSchoolUseCase
import io.github.edmaputra.ilmiya.core.school.domain.port.out.SchoolRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

class LocalDataSeederTest {

    private val schoolRepository = mockk<SchoolRepository>()
    private val registerSchoolUseCase = mockk<RegisterSchoolUseCase>()
    private val createAcademicYearUseCase = mockk<CreateAcademicYearUseCase>()
    private val activateAcademicYearUseCase = mockk<ActivateAcademicYearUseCase>()
    private val addAcademicTermUseCase = mockk<AddAcademicTermUseCase>()
    private val activateAcademicTermUseCase = mockk<ActivateAcademicTermUseCase>()
    private val manageUserUseCase = mockk<ManageUserUseCase>()
    private val manageRoleUseCase = mockk<ManageRoleUseCase>()
    private val userRepository = mockk<UserRepository>()
    private val roleRepository = mockk<RoleRepository>()

    private lateinit var seeder: LocalDataSeeder

    private val sampleTenantId = TenantId.of(LocalDataSeeder.SAMPLE_TENANT_ID)
    private val sampleIamTenantId = IamTenantId.from(LocalDataSeeder.SAMPLE_TENANT_ID)

    @BeforeEach
    fun setUp() {
        seeder = LocalDataSeeder(
            schoolRepository = schoolRepository,
            registerSchoolUseCase = registerSchoolUseCase,
            createAcademicYearUseCase = createAcademicYearUseCase,
            activateAcademicYearUseCase = activateAcademicYearUseCase,
            addAcademicTermUseCase = addAcademicTermUseCase,
            activateAcademicTermUseCase = activateAcademicTermUseCase,
            manageUserUseCase = manageUserUseCase,
            manageRoleUseCase = manageRoleUseCase,
            userRepository = userRepository,
            roleRepository = roleRepository
        )
    }

    @Test
    fun `should seed school, academic year, terms, and IAM data when absent`() {
        // Arrange - School & Academic
        every { schoolRepository.existsByTenantId(sampleTenantId) } returns false

        val sampleSchool = School(
            id = SchoolId.generate(),
            tenantId = sampleTenantId,
            name = "Springfield Green Academy",
            institutionCode = "SGA-001",
            address = "742 Evergreen Terrace",
            contactEmail = LocalDataSeeder.ADMIN_EMAIL,
            contactPhone = "+1-555-0199"
        )
        every { registerSchoolUseCase.register(any<RegisterSchoolCommand>()) } returns sampleSchool

        val sampleAcademicYear = AcademicYear.create(
            tenantId = sampleTenantId,
            name = "2026/2027",
            startDate = LocalDate.of(2026, 7, 1),
            endDate = LocalDate.of(2027, 6, 30)
        )
        every { createAcademicYearUseCase.create(sampleTenantId, any<CreateAcademicYearCommand>()) } returns sampleAcademicYear
        every { activateAcademicYearUseCase.activate(sampleTenantId, sampleAcademicYear.id) } returns sampleAcademicYear

        val sampleTerm1 = AcademicTerm(
            id = AcademicTermId.generate(),
            name = "Semester 1",
            type = AcademicTermType.SEMESTER_1,
            displayName = "Odd Semester 2026/2027",
            startDate = LocalDate.of(2026, 7, 1),
            endDate = LocalDate.of(2026, 12, 31)
        )
        val sampleTerm2 = AcademicTerm(
            id = AcademicTermId.generate(),
            name = "Semester 2",
            type = AcademicTermType.SEMESTER_2,
            displayName = "Even Semester 2026/2027",
            startDate = LocalDate.of(2027, 1, 1),
            endDate = LocalDate.of(2027, 6, 30)
        )

        every {
            addAcademicTermUseCase.addTerm(sampleTenantId, sampleAcademicYear.id, match { it.name == "Semester 1" })
        } returns sampleTerm1

        every {
            addAcademicTermUseCase.addTerm(sampleTenantId, sampleAcademicYear.id, match { it.name == "Semester 2" })
        } returns sampleTerm2

        every {
            activateAcademicTermUseCase.activateTerm(sampleTenantId, sampleAcademicYear.id, sampleTerm1.id)
        } returns sampleTerm1

        // Arrange - IAM
        every { userRepository.existsByEmail(LocalDataSeeder.ADMIN_EMAIL) } returns false
        every { userRepository.existsByEmail(LocalDataSeeder.SUPERADMIN_EMAIL) } returns false
        every { roleRepository.existsByTenantIdAndCode(sampleIamTenantId, "SCHOOL_ADMIN") } returns false

        val sampleRole = Role(
            RoleId.generate(),
            sampleIamTenantId,
            "SCHOOL_ADMIN",
            "School Administrator",
            "Full administrative access for the school tenant",
            false,
            emptySet(),
            Instant.now(),
            Instant.now()
        )
        every { manageRoleUseCase.createRole(any<RoleCommands.CreateRoleCommand>()) } returns sampleRole

        val sampleAdminUser = User(
            UserId.generate(),
            LocalDataSeeder.ADMIN_EMAIL,
            "hashed-pw",
            "Springfield Admin",
            UserStatus.ACTIVE,
            false,
            Instant.now(),
            Instant.now()
        )
        every { manageUserUseCase.createUser(match { it.email == LocalDataSeeder.ADMIN_EMAIL }) } returns sampleAdminUser

        val sampleSuperAdminUser = User(
            UserId.generate(),
            LocalDataSeeder.SUPERADMIN_EMAIL,
            "hashed-pw",
            "Platform Super Admin",
            UserStatus.ACTIVE,
            true,
            Instant.now(),
            Instant.now()
        )
        every { manageUserUseCase.createUser(match { it.email == LocalDataSeeder.SUPERADMIN_EMAIL }) } returns sampleSuperAdminUser

        val sampleAssignment = mockk<UserRoleAssignment>()
        every { manageUserUseCase.assignRole(any<UserCommands.AssignUserRoleCommand>()) } returns sampleAssignment

        // Act
        seeder.run(mockk())

        // Assert
        verify(exactly = 1) { registerSchoolUseCase.register(match { it.tenantId == sampleTenantId.value }) }
        verify(exactly = 1) { createAcademicYearUseCase.create(sampleTenantId, match { it.name == "2026/2027" }) }
        verify(exactly = 1) { activateAcademicYearUseCase.activate(sampleTenantId, sampleAcademicYear.id) }
        verify(exactly = 2) { addAcademicTermUseCase.addTerm(sampleTenantId, sampleAcademicYear.id, any()) }
        verify(exactly = 1) { activateAcademicTermUseCase.activateTerm(sampleTenantId, sampleAcademicYear.id, sampleTerm1.id) }
        verify(exactly = 1) { manageRoleUseCase.createRole(match { it.code == "SCHOOL_ADMIN" }) }
        verify(exactly = 1) { manageUserUseCase.createUser(match { it.email == LocalDataSeeder.ADMIN_EMAIL }) }
        verify(exactly = 1) { manageUserUseCase.createUser(match { it.email == LocalDataSeeder.SUPERADMIN_EMAIL }) }
        verify(exactly = 1) { manageUserUseCase.assignRole(match { it.userId == sampleAdminUser.id && it.roleId == sampleRole.id }) }
    }

    @Test
    fun `should skip seeding when data already exists`() {
        // Arrange
        every { schoolRepository.existsByTenantId(sampleTenantId) } returns true
        every { userRepository.existsByEmail(LocalDataSeeder.ADMIN_EMAIL) } returns true
        every { userRepository.existsByEmail(LocalDataSeeder.SUPERADMIN_EMAIL) } returns true

        // Act
        seeder.run(mockk())

        // Assert
        verify(exactly = 0) { registerSchoolUseCase.register(any()) }
        verify(exactly = 0) { createAcademicYearUseCase.create(any(), any()) }
        verify(exactly = 0) { manageRoleUseCase.createRole(any()) }
        verify(exactly = 0) { manageUserUseCase.createUser(any()) }
    }
}
