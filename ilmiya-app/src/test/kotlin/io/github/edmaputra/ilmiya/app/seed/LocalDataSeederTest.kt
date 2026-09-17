package io.github.edmaputra.ilmiya.app.seed

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
import java.time.LocalDate

class LocalDataSeederTest {

    private val schoolRepository = mockk<SchoolRepository>()
    private val registerSchoolUseCase = mockk<RegisterSchoolUseCase>()
    private val createAcademicYearUseCase = mockk<CreateAcademicYearUseCase>()
    private val activateAcademicYearUseCase = mockk<ActivateAcademicYearUseCase>()
    private val addAcademicTermUseCase = mockk<AddAcademicTermUseCase>()
    private val activateAcademicTermUseCase = mockk<ActivateAcademicTermUseCase>()

    private lateinit var seeder: LocalDataSeeder

    private val sampleTenantId = TenantId.of("tenant-springfield")

    @BeforeEach
    fun setUp() {
        seeder = LocalDataSeeder(
            schoolRepository = schoolRepository,
            registerSchoolUseCase = registerSchoolUseCase,
            createAcademicYearUseCase = createAcademicYearUseCase,
            activateAcademicYearUseCase = activateAcademicYearUseCase,
            addAcademicTermUseCase = addAcademicTermUseCase,
            activateAcademicTermUseCase = activateAcademicTermUseCase
        )
    }

    @Test
    fun `should seed school, academic year, and terms when tenant does not exist`() {
        // Arrange
        every { schoolRepository.existsByTenantId(sampleTenantId) } returns false

        val sampleSchool = School(
            id = SchoolId.generate(),
            tenantId = sampleTenantId,
            name = "Springfield Green Academy",
            institutionCode = "SGA-001",
            address = "742 Evergreen Terrace",
            contactEmail = "admin@springfield.local",
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

        // Act
        seeder.run(mockk())

        // Assert
        verify(exactly = 1) { registerSchoolUseCase.register(match { it.tenantId == sampleTenantId.value }) }
        verify(exactly = 1) { createAcademicYearUseCase.create(sampleTenantId, match { it.name == "2026/2027" }) }
        verify(exactly = 1) { activateAcademicYearUseCase.activate(sampleTenantId, sampleAcademicYear.id) }
        verify(exactly = 2) { addAcademicTermUseCase.addTerm(sampleTenantId, sampleAcademicYear.id, any()) }
        verify(exactly = 1) { activateAcademicTermUseCase.activateTerm(sampleTenantId, sampleAcademicYear.id, sampleTerm1.id) }
    }

    @Test
    fun `should skip seeding when tenant already exists`() {
        // Arrange
        every { schoolRepository.existsByTenantId(sampleTenantId) } returns true

        // Act
        seeder.run(mockk())

        // Assert
        verify(exactly = 0) { registerSchoolUseCase.register(any()) }
        verify(exactly = 0) { createAcademicYearUseCase.create(any(), any()) }
    }
}
