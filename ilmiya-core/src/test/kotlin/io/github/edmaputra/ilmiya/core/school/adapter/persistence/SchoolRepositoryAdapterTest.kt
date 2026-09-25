package io.github.edmaputra.ilmiya.core.school.adapter.persistence

import io.github.edmaputra.ilmiya.common.domain.TenantId
import io.github.edmaputra.ilmiya.core.school.domain.Accreditation
import io.github.edmaputra.ilmiya.core.school.domain.GradingPreference
import io.github.edmaputra.ilmiya.core.school.domain.OperationalSettings
import io.github.edmaputra.ilmiya.core.school.domain.School
import io.github.edmaputra.ilmiya.core.school.domain.SchoolId
import io.github.edmaputra.ilmiya.core.school.domain.SchoolStatus
import io.github.edmaputra.ilmiya.core.school.domain.TermLayout
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.util.Optional

class SchoolRepositoryAdapterTest {

    private val springDataRepository = mockk<SpringDataSchoolRepository>()
    private val adapter = SchoolRepositoryAdapter(springDataRepository)

    private val tenantId = TenantId.of("tenant-01")
    private val schoolId = SchoolId.of("sch-01")

    private val sampleDomainSchool = School(
        id = schoolId,
        tenantId = tenantId,
        name = "Global School",
        institutionCode = "GS01",
        governmentRegistrationNumber = "NPSN123",
        registrationType = "NPSN",
        address = "Jl. Sudirman 10",
        contactEmail = "admin@gs01.sch.id",
        contactPhone = "021-999999",
        logoUrl = "https://example.com/logo.png",
        accreditation = Accreditation(
            status = "A",
            accreditingBody = "BAN-S/M",
            validUntil = LocalDate.of(2030, 1, 1)
        ),
        operationalSettings = OperationalSettings(
            workingDays = setOf(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY),
            gradingPreference = GradingPreference.NUMERIC_0_100,
            termLayout = TermLayout.SEMESTER
        ),
        status = SchoolStatus.ACTIVE,
        createdAt = Instant.now(),
        updatedAt = Instant.now()
    )

    private val sampleJpaEntity = SchoolJpaEntity(
        id = schoolId.value,
        tenantId = tenantId.value,
        name = "Global School",
        institutionCode = "GS01",
        governmentRegistrationNumber = "NPSN123",
        registrationType = "NPSN",
        address = "Jl. Sudirman 10",
        contactEmail = "admin@gs01.sch.id",
        contactPhone = "021-999999",
        logoUrl = "https://example.com/logo.png",
        accreditationStatus = "A",
        accreditationBody = "BAN-S/M",
        accreditationValidUntil = LocalDate.of(2030, 1, 1),
        workingDays = "MONDAY,TUESDAY,WEDNESDAY",
        gradingPreference = "NUMERIC_0_100",
        termLayout = "SEMESTER",
        status = "ACTIVE",
        createdAt = Instant.now(),
        updatedAt = Instant.now()
    )

    @Test
    fun `save when school does not exist should map domain to new entity, save to spring data repository, and map back to domain`() {
        val entitySlot = slot<SchoolJpaEntity>()
        every { springDataRepository.findById(schoolId.value) } returns Optional.empty()
        every { springDataRepository.save(capture(entitySlot)) } returns sampleJpaEntity

        val result = adapter.save(sampleDomainSchool)

        assertThat(entitySlot.isCaptured).isTrue()
        val captured = entitySlot.captured
        assertThat(captured.id).isEqualTo(schoolId.value)
        assertThat(captured.tenantId).isEqualTo(tenantId.value)
        assertThat(captured.name).isEqualTo("Global School")
        assertThat(captured.institutionCode).isEqualTo("GS01")
        assertThat(captured.workingDays).isEqualTo("MONDAY,TUESDAY,WEDNESDAY")

        assertThat(result.id).isEqualTo(schoolId)
        assertThat(result.name).isEqualTo("Global School")
        assertThat(result.operationalSettings.workingDays).containsExactlyInAnyOrder(
            DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY
        )
        verify(exactly = 1) { springDataRepository.findById(schoolId.value) }
        verify(exactly = 1) { springDataRepository.save(any()) }
    }

    @Test
    fun `save when school already exists should update existing entity, save to spring data repository, and map back to domain`() {
        val existingEntity = SchoolJpaEntity(
            id = schoolId.value,
            tenantId = "old-tenant",
            name = "Old School",
            institutionCode = "OLD01",
            governmentRegistrationNumber = "OLD-REG",
            registrationType = "NPSN",
            address = "Old Address",
            contactEmail = "old@school.com",
            contactPhone = "000-000",
            logoUrl = null,
            accreditationStatus = "B",
            accreditationBody = "BAN",
            accreditationValidUntil = LocalDate.of(2028, 1, 1),
            workingDays = "MONDAY",
            gradingPreference = "PERCENTAGE_0_100",
            termLayout = "TRIMESTER",
            status = "PENDING_APPROVAL"
        )

        every { springDataRepository.findById(schoolId.value) } returns Optional.of(existingEntity)
        every { springDataRepository.save(existingEntity) } returns existingEntity

        val result = adapter.save(sampleDomainSchool)

        assertThat(existingEntity.name).isEqualTo("Global School")
        assertThat(existingEntity.tenantId).isEqualTo(tenantId.value)
        assertThat(existingEntity.institutionCode).isEqualTo("GS01")
        assertThat(existingEntity.workingDays).isEqualTo("MONDAY,TUESDAY,WEDNESDAY")
        assertThat(existingEntity.status).isEqualTo("ACTIVE")

        assertThat(result.id).isEqualTo(schoolId)
        assertThat(result.name).isEqualTo("Global School")
        verify(exactly = 1) { springDataRepository.findById(schoolId.value) }
        verify(exactly = 1) { springDataRepository.save(existingEntity) }
    }

    @Test
    fun `findById should return domain school when entity found`() {
        every { springDataRepository.findById(schoolId.value) } returns Optional.of(sampleJpaEntity)

        val result = adapter.findById(schoolId)

        assertThat(result).isNotNull
        assertThat(result?.id).isEqualTo(schoolId)
        assertThat(result?.name).isEqualTo("Global School")
    }

    @Test
    fun `findById should return null when entity not found`() {
        every { springDataRepository.findById(schoolId.value) } returns Optional.empty()

        val result = adapter.findById(schoolId)

        assertThat(result).isNull()
    }

    @Test
    fun `findByTenantId should return domain school when found`() {
        every { springDataRepository.findByTenantId(tenantId.value) } returns Optional.of(sampleJpaEntity)

        val result = adapter.findByTenantId(tenantId)

        assertThat(result).isNotNull
        assertThat(result?.tenantId).isEqualTo(tenantId)
    }

    @Test
    fun `findByTenantId should return null when not found`() {
        every { springDataRepository.findByTenantId(tenantId.value) } returns Optional.empty()

        val result = adapter.findByTenantId(tenantId)

        assertThat(result).isNull()
    }

    @Test
    fun `findByInstitutionCode should return domain school when found`() {
        every { springDataRepository.findByInstitutionCode("GS01") } returns Optional.of(sampleJpaEntity)

        val result = adapter.findByInstitutionCode("GS01")

        assertThat(result).isNotNull
        assertThat(result?.institutionCode).isEqualTo("GS01")
    }

    @Test
    fun `findByInstitutionCode should return null when not found`() {
        every { springDataRepository.findByInstitutionCode("UNKNOWN") } returns Optional.empty()

        val result = adapter.findByInstitutionCode("UNKNOWN")

        assertThat(result).isNull()
    }

    @Test
    fun `existsByInstitutionCode should delegate to spring data repository`() {
        every { springDataRepository.existsByInstitutionCode("GS01") } returns true
        every { springDataRepository.existsByInstitutionCode("OTHER") } returns false

        assertThat(adapter.existsByInstitutionCode("GS01")).isTrue()
        assertThat(adapter.existsByInstitutionCode("OTHER")).isFalse()
    }

    @Test
    fun `existsByTenantId should delegate to spring data repository`() {
        every { springDataRepository.existsByTenantId(tenantId.value) } returns true

        assertThat(adapter.existsByTenantId(tenantId)).isTrue()
    }

    @Test
    fun `toDomain should fallback to default working days when entity workingDays is empty`() {
        val emptyDaysEntity = SchoolJpaEntity(
            id = schoolId.value,
            tenantId = tenantId.value,
            name = "Default Days School",
            institutionCode = "DDS",
            address = "Jl. Default",
            contactEmail = "default@school.com",
            contactPhone = "123",
            workingDays = "",
            gradingPreference = "NUMERIC_0_100",
            termLayout = "SEMESTER",
            status = "ACTIVE"
        )
        every { springDataRepository.findById(schoolId.value) } returns Optional.of(emptyDaysEntity)

        val result = adapter.findById(schoolId)

        assertThat(result).isNotNull
        assertThat(result?.operationalSettings?.workingDays).containsExactlyInAnyOrder(
            DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY
        )
    }

    @Test
    fun `findAll should return mapped domain schools from spring data repository page`() {
        val pageableSlot = slot<org.springframework.data.domain.Pageable>()
        val page = org.springframework.data.domain.PageImpl(listOf(sampleJpaEntity))
        every { springDataRepository.findAll(capture(pageableSlot)) } returns page

        val results = adapter.findAll(page = 0, size = 10)

        assertThat(results).hasSize(1)
        assertThat(results[0].name).isEqualTo("Global School")
        assertThat(pageableSlot.captured.pageNumber).isEqualTo(0)
        assertThat(pageableSlot.captured.pageSize).isEqualTo(10)
        verify(exactly = 1) { springDataRepository.findAll(any<org.springframework.data.domain.Pageable>()) }
    }

    @Test
    fun `count should delegate to spring data repository`() {
        every { springDataRepository.count() } returns 42L

        val result = adapter.count()

        assertThat(result).isEqualTo(42L)
        verify(exactly = 1) { springDataRepository.count() }
    }

    @Test
    fun `deleteByTenantId should find entity and delete when present`() {
        every { springDataRepository.findByTenantId(tenantId.value) } returns Optional.of(sampleJpaEntity)
        every { springDataRepository.delete(sampleJpaEntity) } returns Unit

        adapter.deleteByTenantId(tenantId)

        verify(exactly = 1) { springDataRepository.delete(sampleJpaEntity) }
    }

    @Test
    fun `deleteByTenantId should not delete when entity is not found`() {
        every { springDataRepository.findByTenantId(tenantId.value) } returns Optional.empty()

        adapter.deleteByTenantId(tenantId)

        verify(exactly = 0) { springDataRepository.delete(any()) }
    }
}
