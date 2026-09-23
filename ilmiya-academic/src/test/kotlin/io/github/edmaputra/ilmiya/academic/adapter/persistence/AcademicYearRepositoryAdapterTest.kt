package io.github.edmaputra.ilmiya.academic.adapter.persistence

import io.github.edmaputra.ilmiya.academic.domain.AcademicTerm
import io.github.edmaputra.ilmiya.academic.domain.AcademicTermId
import io.github.edmaputra.ilmiya.academic.domain.AcademicTermStatus
import io.github.edmaputra.ilmiya.academic.domain.AcademicTermType
import io.github.edmaputra.ilmiya.academic.domain.AcademicYear
import io.github.edmaputra.ilmiya.academic.domain.AcademicYearId
import io.github.edmaputra.ilmiya.academic.domain.AcademicYearStatus
import io.github.edmaputra.ilmiya.common.domain.TenantId
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.LocalDate
import java.util.Optional

class AcademicYearRepositoryAdapterTest {

    private val springDataRepository = mockk<SpringDataAcademicYearRepository>()
    private val adapter = AcademicYearRepositoryAdapter(springDataRepository)

    private val tenantId = TenantId.of("tenant-01")
    private val yearId = AcademicYearId.of("ay-01")
    private val termId = AcademicTermId.of("term-01")

    private val sampleTerm = AcademicTerm(
        id = termId,
        name = "Semester 1",
        type = AcademicTermType.SEMESTER_1,
        displayName = "Semester Ganjil",
        startDate = LocalDate.of(2026, 7, 1),
        endDate = LocalDate.of(2026, 12, 31),
        status = AcademicTermStatus.ACTIVE
    )

    private val sampleYear = AcademicYear(
        id = yearId,
        tenantId = tenantId,
        name = "2026/2027",
        startDate = LocalDate.of(2026, 7, 1),
        endDate = LocalDate.of(2027, 6, 30),
        status = AcademicYearStatus.ACTIVE,
        terms = listOf(sampleTerm),
        createdAt = Instant.now(),
        updatedAt = Instant.now()
    )

    private val sampleTermEntity = AcademicTermJpaEntity(
        id = termId.value,
        academicYearId = yearId.value,
        name = "Semester 1",
        termType = "SEMESTER_1",
        displayName = "Semester Ganjil",
        startDate = LocalDate.of(2026, 7, 1),
        endDate = LocalDate.of(2026, 12, 31),
        status = "ACTIVE"
    )

    private val sampleYearEntity = AcademicYearJpaEntity(
        id = yearId.value,
        tenantId = tenantId.value,
        name = "2026/2027",
        startDate = LocalDate.of(2026, 7, 1),
        endDate = LocalDate.of(2027, 6, 30),
        status = "ACTIVE",
        createdAt = Instant.now(),
        updatedAt = Instant.now()
    ).apply {
        terms = mutableListOf(sampleTermEntity)
    }

    @Test
    fun `save when academic year does not exist should map domain to new entity, save to repository, and map back to domain`() {
        val entitySlot = slot<AcademicYearJpaEntity>()
        every { springDataRepository.findById(yearId.value) } returns Optional.empty()
        every { springDataRepository.save(capture(entitySlot)) } returns sampleYearEntity

        val result = adapter.save(sampleYear)

        assertThat(entitySlot.isCaptured).isTrue()
        val captured = entitySlot.captured
        assertThat(captured.id).isEqualTo(yearId.value)
        assertThat(captured.tenantId).isEqualTo(tenantId.value)
        assertThat(captured.name).isEqualTo("2026/2027")
        assertThat(captured.terms).hasSize(1)
        assertThat(captured.terms[0].id).isEqualTo(termId.value)

        assertThat(result.id).isEqualTo(yearId)
        assertThat(result.name).isEqualTo("2026/2027")
        assertThat(result.terms).hasSize(1)
        assertThat(result.terms[0].name).isEqualTo("Semester 1")
        verify(exactly = 1) { springDataRepository.findById(yearId.value) }
        verify(exactly = 1) { springDataRepository.save(any()) }
    }

    @Test
    fun `save when academic year already exists should update existing entity, save to repository, and map back to domain`() {
        val existingEntity = AcademicYearJpaEntity(
            id = yearId.value,
            tenantId = "old-tenant",
            name = "Old Year",
            startDate = LocalDate.of(2025, 7, 1),
            endDate = LocalDate.of(2026, 6, 30),
            status = "PLANNED"
        ).apply {
            terms = mutableListOf(
                AcademicTermJpaEntity(
                    id = "old-term",
                    academicYearId = yearId.value,
                    name = "Old Term",
                    termType = "SEMESTER_1",
                    displayName = "Old Display",
                    startDate = LocalDate.of(2025, 7, 1),
                    endDate = LocalDate.of(2025, 12, 31),
                    status = "PLANNED"
                )
            )
        }

        every { springDataRepository.findById(yearId.value) } returns Optional.of(existingEntity)
        every { springDataRepository.save(existingEntity) } returns existingEntity

        val result = adapter.save(sampleYear)

        assertThat(existingEntity.name).isEqualTo("2026/2027")
        assertThat(existingEntity.tenantId).isEqualTo(tenantId.value)
        assertThat(existingEntity.status).isEqualTo(AcademicYearStatus.ACTIVE.name)
        assertThat(existingEntity.terms).hasSize(1)
        assertThat(existingEntity.terms[0].id).isEqualTo(termId.value)
        assertThat(existingEntity.terms[0].name).isEqualTo("Semester 1")

        assertThat(result.id).isEqualTo(yearId)
        assertThat(result.name).isEqualTo("2026/2027")
        verify(exactly = 1) { springDataRepository.findById(yearId.value) }
        verify(exactly = 1) { springDataRepository.save(existingEntity) }
    }

    @Test
    fun `findById should return domain academic year when entity found`() {
        every { springDataRepository.findByTenantIdAndId(tenantId.value, yearId.value) } returns Optional.of(sampleYearEntity)

        val result = adapter.findById(tenantId, yearId)

        assertThat(result).isNotNull
        assertThat(result?.id).isEqualTo(yearId)
        assertThat(result?.terms).hasSize(1)
    }

    @Test
    fun `findById should return null when not found`() {
        every { springDataRepository.findByTenantIdAndId(tenantId.value, yearId.value) } returns Optional.empty()

        val result = adapter.findById(tenantId, yearId)

        assertThat(result).isNull()
    }

    @Test
    fun `findByName should return domain academic year when found`() {
        every { springDataRepository.findByTenantIdAndName(tenantId.value, "2026/2027") } returns Optional.of(sampleYearEntity)

        val result = adapter.findByName(tenantId, "2026/2027")

        assertThat(result).isNotNull
        assertThat(result?.name).isEqualTo("2026/2027")
    }

    @Test
    fun `findByName should return null when not found`() {
        every { springDataRepository.findByTenantIdAndName(tenantId.value, "unknown") } returns Optional.empty()

        val result = adapter.findByName(tenantId, "unknown")

        assertThat(result).isNull()
    }

    @Test
    fun `findActive should return active domain academic year when found`() {
        every { springDataRepository.findByTenantIdAndStatus(tenantId.value, AcademicYearStatus.ACTIVE.name) } returns Optional.of(sampleYearEntity)

        val result = adapter.findActive(tenantId)

        assertThat(result).isNotNull
        assertThat(result?.status).isEqualTo(AcademicYearStatus.ACTIVE)
    }

    @Test
    fun `findActive should return null when no active year`() {
        every { springDataRepository.findByTenantIdAndStatus(tenantId.value, AcademicYearStatus.ACTIVE.name) } returns Optional.empty()

        val result = adapter.findActive(tenantId)

        assertThat(result).isNull()
    }

    @Test
    fun `findAllByTenantId should return list of domain academic years`() {
        every { springDataRepository.findAllByTenantIdOrderByStartDateDesc(tenantId.value) } returns listOf(sampleYearEntity)

        val result = adapter.findAllByTenantId(tenantId)

        assertThat(result).hasSize(1)
        assertThat(result[0].id).isEqualTo(yearId)
    }

    @Test
    fun `existsByName should delegate to repository`() {
        every { springDataRepository.existsByTenantIdAndName(tenantId.value, "2026/2027") } returns true
        every { springDataRepository.existsByTenantIdAndName(tenantId.value, "other") } returns false

        assertThat(adapter.existsByName(tenantId, "2026/2027")).isTrue()
        assertThat(adapter.existsByName(tenantId, "other")).isFalse()
    }
}
