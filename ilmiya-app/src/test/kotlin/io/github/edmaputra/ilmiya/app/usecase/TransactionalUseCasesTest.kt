package io.github.edmaputra.ilmiya.app.usecase

import io.github.edmaputra.ilmiya.academic.domain.AcademicTermType
import io.github.edmaputra.ilmiya.academic.domain.AcademicYearStatus
import io.github.edmaputra.ilmiya.academic.domain.port.`in`.AddAcademicTermCommand
import io.github.edmaputra.ilmiya.academic.domain.port.`in`.CreateAcademicYearCommand
import io.github.edmaputra.ilmiya.academic.domain.port.out.AcademicYearEventPublisher
import io.github.edmaputra.ilmiya.academic.domain.port.out.AcademicYearRepository
import io.github.edmaputra.ilmiya.app.academic.TransactionalAcademicYearUseCase
import io.github.edmaputra.ilmiya.app.school.TransactionalConfigureSchoolSettingsUseCase
import io.github.edmaputra.ilmiya.app.school.TransactionalListSchoolsUseCase
import io.github.edmaputra.ilmiya.app.school.TransactionalRegisterSchoolUseCase
import io.github.edmaputra.ilmiya.app.school.TransactionalSchoolProfileUseCase
import io.github.edmaputra.ilmiya.app.tenant.TransactionalListTenantsUseCase
import io.github.edmaputra.ilmiya.app.tenant.TransactionalManageTenantUseCase
import io.github.edmaputra.ilmiya.common.domain.TenantId
import io.github.edmaputra.ilmiya.core.school.domain.GradingPreference
import io.github.edmaputra.ilmiya.core.school.domain.School
import io.github.edmaputra.ilmiya.core.school.domain.TermLayout
import io.github.edmaputra.ilmiya.core.school.domain.port.`in`.ConfigureSchoolSettingsCommand
import io.github.edmaputra.ilmiya.core.school.domain.port.`in`.RegisterSchoolCommand
import io.github.edmaputra.ilmiya.core.school.domain.port.`in`.UpdateSchoolProfileCommand
import io.github.edmaputra.ilmiya.core.school.domain.port.out.SchoolEventPublisher
import io.github.edmaputra.ilmiya.core.school.domain.port.out.SchoolRepository
import io.github.edmaputra.ilmiya.core.tenant.domain.Tenant
import io.github.edmaputra.ilmiya.core.tenant.domain.TenantStatus
import io.github.edmaputra.ilmiya.core.tenant.domain.port.`in`.CreateTenantCommand
import io.github.edmaputra.ilmiya.core.tenant.domain.port.`in`.UpdateTenantCommand
import io.github.edmaputra.ilmiya.core.tenant.domain.port.out.TenantEventPublisher
import io.github.edmaputra.ilmiya.core.tenant.domain.port.out.TenantRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.DayOfWeek
import java.time.LocalDate

class TransactionalUseCasesTest {

    private val tenantId = TenantId.of("tenant-01")

    // School mocks
    private val schoolRepository = mockk<SchoolRepository>()
    private val schoolEventPublisher = mockk<SchoolEventPublisher>(relaxed = true)

    // Tenant mocks
    private val tenantRepository = mockk<TenantRepository>()
    private val tenantEventPublisher = mockk<TenantEventPublisher>(relaxed = true)

    // Academic year mocks
    private val academicYearRepository = mockk<AcademicYearRepository>()
    private val academicYearEventPublisher = mockk<AcademicYearEventPublisher>(relaxed = true)

    @Test
    fun `TransactionalRegisterSchoolUseCase should delegate registration`() {
        val useCase = TransactionalRegisterSchoolUseCase(schoolRepository, schoolEventPublisher)
        val command = RegisterSchoolCommand(
            tenantId = "tenant-01",
            name = "Test School",
            institutionCode = "TS01",
            address = "Jl. Test 1",
            contactEmail = "admin@ts.sch.id",
            contactPhone = "021-111111"
        )

        every { schoolRepository.existsByInstitutionCode("TS01") } returns false
        every { schoolRepository.existsByTenantId(tenantId) } returns false
        every { schoolRepository.save(any()) } answers { firstArg() }

        val result = useCase.register(command)
        assertThat(result.name).isEqualTo("Test School")
        verify(exactly = 1) { schoolRepository.save(any()) }
    }

    @Test
    fun `TransactionalSchoolProfileUseCase should delegate getProfile and updateProfile`() {
        val useCase = TransactionalSchoolProfileUseCase(schoolRepository, schoolEventPublisher)
        val existingSchool = School.create(
            tenantId = tenantId,
            name = "Old School",
            institutionCode = "OS01",
            address = "Jl. Old",
            contactEmail = "admin@old.sch.id",
            contactPhone = "021-222222"
        )
        existingSchool.pullDomainEvents()

        every { schoolRepository.findByTenantId(tenantId) } returns existingSchool
        every { schoolRepository.save(any()) } answers { firstArg() }

        val profile = useCase.getProfile(tenantId)
        assertThat(profile.name).isEqualTo("Old School")

        val updateCommand = UpdateSchoolProfileCommand(
            name = "New School",
            address = "Jl. New",
            contactEmail = "admin@new.sch.id",
            contactPhone = "021-333333"
        )
        val updated = useCase.updateProfile(tenantId, updateCommand)
        assertThat(updated.name).isEqualTo("New School")
    }

    @Test
    fun `TransactionalConfigureSchoolSettingsUseCase should delegate configureSettings`() {
        val useCase = TransactionalConfigureSchoolSettingsUseCase(schoolRepository, schoolEventPublisher)
        val existingSchool = School.create(
            tenantId = tenantId,
            name = "Settings School",
            institutionCode = "SS01",
            address = "Jl. Settings",
            contactEmail = "admin@ss.sch.id",
            contactPhone = "021-444444"
        )
        existingSchool.pullDomainEvents()

        every { schoolRepository.findByTenantId(tenantId) } returns existingSchool
        every { schoolRepository.save(any()) } answers { firstArg() }

        val configureCommand = ConfigureSchoolSettingsCommand(
            workingDays = setOf(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY),
            gradingPreference = GradingPreference.LETTER_A_E,
            termLayout = TermLayout.TRIMESTER
        )
        val updated = useCase.configureSettings(tenantId, configureCommand)
        assertThat(updated.operationalSettings.termLayout).isEqualTo(TermLayout.TRIMESTER)
    }

    @Test
    fun `TransactionalAcademicYearUseCase should delegate operations`() {
        val useCase = TransactionalAcademicYearUseCase(academicYearRepository, academicYearEventPublisher)

        val createCmd = CreateAcademicYearCommand(
            name = "2026/2027",
            startDate = LocalDate.of(2026, 7, 1),
            endDate = LocalDate.of(2027, 6, 30)
        )

        every { academicYearRepository.existsByName(tenantId, "2026/2027") } returns false
        every { academicYearRepository.save(any()) } answers { firstArg() }

        val created = useCase.create(tenantId, createCmd)
        assertThat(created.name).isEqualTo("2026/2027")

        every { academicYearRepository.findById(tenantId, created.id) } returns created
        every { academicYearRepository.findActive(tenantId) } returns created
        every { academicYearRepository.findAllByTenantId(tenantId) } returns listOf(created)

        val byId = useCase.getById(tenantId, created.id)
        assertThat(byId.id).isEqualTo(created.id)

        val active = useCase.getActiveAcademicYear(tenantId)
        assertThat(active?.id).isEqualTo(created.id)

        val list = useCase.listByTenant(tenantId)
        assertThat(list).hasSize(1)

        val activated = useCase.activate(tenantId, created.id)
        assertThat(activated.status).isEqualTo(AcademicYearStatus.ACTIVE)

        val addTermCmd = AddAcademicTermCommand(
            name = "Semester 1",
            type = AcademicTermType.SEMESTER_1,
            displayName = "Semester Ganjil",
            startDate = LocalDate.of(2026, 7, 1),
            endDate = LocalDate.of(2026, 12, 31)
        )
        val term = useCase.addTerm(tenantId, created.id, addTermCmd)
        assertThat(term.name).isEqualTo("Semester 1")

        val yearWithTerm = created.addTerm(term)
        every { academicYearRepository.findById(tenantId, created.id) } returns yearWithTerm

        val activatedTerm = useCase.activateTerm(tenantId, created.id, term.id)
        assertThat(activatedTerm.id).isEqualTo(term.id)
    }

    @Test
    fun `TransactionalListSchoolsUseCase should delegate listSchools`() {
        val useCase = TransactionalListSchoolsUseCase(schoolRepository)
        val school = School.create(
            tenantId = tenantId,
            name = "Test School",
            institutionCode = "TS01",
            address = "Jl. Test 1",
            contactEmail = "admin@ts.sch.id",
            contactPhone = "021-111111"
        )
        every { schoolRepository.findAll(0, 20) } returns listOf(school)
        every { schoolRepository.count() } returns 1L

        val result = useCase.listSchools(0, 20)

        assertThat(result.content).hasSize(1)
        assertThat(result.content[0].name).isEqualTo("Test School")
        assertThat(result.totalElements).isEqualTo(1L)
        verify(exactly = 1) { schoolRepository.findAll(0, 20) }
        verify(exactly = 1) { schoolRepository.count() }
    }

    @Test
    fun `TransactionalListTenantsUseCase should delegate listTenants`() {
        val useCase = TransactionalListTenantsUseCase(tenantRepository)
        val tenant = Tenant.create(
            tenantId = tenantId,
            name = "Test Foundation",
            address = "Jl. Test 1",
            contactEmail = "admin@tf.org",
            contactPhone = "021-111111"
        )
        every { tenantRepository.findAll(0, 20) } returns listOf(tenant)
        every { tenantRepository.count() } returns 1L

        val result = useCase.listTenants(0, 20)

        assertThat(result.content).hasSize(1)
        assertThat(result.content[0].name).isEqualTo("Test Foundation")
        assertThat(result.totalElements).isEqualTo(1L)
        verify(exactly = 1) { tenantRepository.findAll(0, 20) }
        verify(exactly = 1) { tenantRepository.count() }
    }

    @Test
    fun `TransactionalManageTenantUseCase should delegate CRUD operations`() {
        val useCase = TransactionalManageTenantUseCase(tenantRepository, tenantEventPublisher)
        val tenant = Tenant.create(
            tenantId = tenantId,
            name = "Test Foundation",
            address = "Jl. Test 1",
            contactEmail = "admin@tf.org",
            contactPhone = "021-111111"
        )

        // Create
        val createCmd = CreateTenantCommand(
            tenantId = "tenant-01",
            name = "Test Foundation",
            address = "Jl. Test 1",
            contactEmail = "admin@tf.org",
            contactPhone = "021-111111"
        )
        every { tenantRepository.existsById(tenantId) } returns false
        every { tenantRepository.save(any()) } returns tenant

        val created = useCase.createTenant(createCmd)
        assertThat(created.name).isEqualTo("Test Foundation")

        // Get
        every { tenantRepository.findById(tenantId) } returns tenant
        val fetched = useCase.getTenant(tenantId)
        assertThat(fetched.name).isEqualTo("Test Foundation")

        // Update
        val updateCmd = UpdateTenantCommand(
            name = "Updated Foundation",
            address = "Jl. Test 2",
            contactEmail = "info@tf.org",
            contactPhone = "021-222222",
            status = TenantStatus.ACTIVE
        )
        every { tenantRepository.save(any()) } returns tenant
        val updated = useCase.updateTenant(tenantId, updateCmd)
        assertThat(updated).isNotNull

        // Delete
        every { tenantRepository.existsById(tenantId) } returns true
        every { tenantRepository.deleteById(tenantId) } returns Unit
        useCase.deleteTenant(tenantId)
        verify(exactly = 1) { tenantRepository.deleteById(tenantId) }
    }
}
