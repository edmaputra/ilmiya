package io.github.edmaputra.ilmiya.academic.application

import io.github.edmaputra.ilmiya.academic.domain.AcademicTermType
import io.github.edmaputra.ilmiya.academic.domain.AcademicYear
import io.github.edmaputra.ilmiya.academic.domain.AcademicYearId
import io.github.edmaputra.ilmiya.academic.domain.AcademicYearStatus
import io.github.edmaputra.ilmiya.academic.domain.exception.DuplicateAcademicYearException
import io.github.edmaputra.ilmiya.academic.domain.port.`in`.AddAcademicTermCommand
import io.github.edmaputra.ilmiya.academic.domain.port.`in`.CreateAcademicYearCommand
import io.github.edmaputra.ilmiya.academic.domain.port.out.AcademicYearEventPublisher
import io.github.edmaputra.ilmiya.academic.domain.port.out.AcademicYearRepository
import io.github.edmaputra.ilmiya.common.domain.TenantId
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.time.LocalDate

class AcademicYearServiceTest {

    private val repository = mockk<AcademicYearRepository>()
    private val eventPublisher = mockk<AcademicYearEventPublisher>(relaxed = true)
    private val service = AcademicYearService(repository, eventPublisher)
    private val tenantId = TenantId.of("tenant-sch-01")

    @Test
    fun `should create academic year when name is unique`() {
        val command = CreateAcademicYearCommand(
            name = "2026/2027",
            startDate = LocalDate.of(2026, 7, 1),
            endDate = LocalDate.of(2027, 6, 30)
        )

        every { repository.existsByName(tenantId, "2026/2027") } returns false
        every { repository.save(any<AcademicYear>()) } answers { firstArg() }

        val result = service.create(tenantId, command)

        assertThat(result.name).isEqualTo("2026/2027")
        assertThat(result.status).isEqualTo(AcademicYearStatus.PLANNED)
        verify(exactly = 1) { repository.save(any<AcademicYear>()) }
        verify(atLeast = 1) { eventPublisher.publishAll(any()) }
    }

    @Test
    fun `should throw DuplicateAcademicYearException when name exists`() {
        val command = CreateAcademicYearCommand(
            name = "2026/2027",
            startDate = LocalDate.of(2026, 7, 1),
            endDate = LocalDate.of(2027, 6, 30)
        )

        every { repository.existsByName(tenantId, "2026/2027") } returns true

        assertThatThrownBy { service.create(tenantId, command) }
            .isInstanceOf(DuplicateAcademicYearException::class.java)
            .hasMessageContaining("already exists")

        verify(exactly = 0) { repository.save(any()) }
    }

    @Test
    fun `should activate target year and archive previously active year`() {
        val previousActive = AcademicYear.create(
            id = AcademicYearId.of("year-prev"),
            tenantId = tenantId,
            name = "2025/2026",
            startDate = LocalDate.of(2025, 7, 1),
            endDate = LocalDate.of(2026, 6, 30)
        ).activate()

        val targetYear = AcademicYear.create(
            id = AcademicYearId.of("year-target"),
            tenantId = tenantId,
            name = "2026/2027",
            startDate = LocalDate.of(2026, 7, 1),
            endDate = LocalDate.of(2027, 6, 30)
        )

        every { repository.findById(tenantId, targetYear.id) } returns targetYear
        every { repository.findActive(tenantId) } returns previousActive
        every { repository.save(any<AcademicYear>()) } answers { firstArg() }

        val activated = service.activate(tenantId, targetYear.id)

        assertThat(activated.status).isEqualTo(AcademicYearStatus.ACTIVE)
        // Should have saved the archived previous active year and the newly activated year
        verify(exactly = 2) { repository.save(any<AcademicYear>()) }
    }

    @Test
    fun `should add term to academic year`() {
        val year = AcademicYear.create(
            id = AcademicYearId.of("year-1"),
            tenantId = tenantId,
            name = "2026/2027",
            startDate = LocalDate.of(2026, 7, 1),
            endDate = LocalDate.of(2027, 6, 30)
        )

        every { repository.findById(tenantId, year.id) } returns year
        every { repository.save(any<AcademicYear>()) } answers { firstArg() }

        val command = AddAcademicTermCommand(
            name = "Semester 1",
            type = AcademicTermType.SEMESTER_1,
            displayName = "Semester Ganjil",
            startDate = LocalDate.of(2026, 7, 15),
            endDate = LocalDate.of(2026, 12, 20)
        )

        val addedTerm = service.addTerm(tenantId, year.id, command)

        assertThat(addedTerm.displayName).isEqualTo("Semester Ganjil")
        assertThat(addedTerm.type).isEqualTo(AcademicTermType.SEMESTER_1)
        verify(exactly = 1) { repository.save(any<AcademicYear>()) }
        verify(atLeast = 1) { eventPublisher.publishAll(any()) }
    }
}
