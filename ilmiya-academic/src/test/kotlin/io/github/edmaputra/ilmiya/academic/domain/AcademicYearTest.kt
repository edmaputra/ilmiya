package io.github.edmaputra.ilmiya.academic.domain

import io.github.edmaputra.ilmiya.academic.domain.exception.InvalidAcademicYearDatesException
import io.github.edmaputra.ilmiya.common.domain.TenantId
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.time.LocalDate

class AcademicYearTest {

    private val tenantId = TenantId.of("tenant-sch-01")

    @Test
    fun `should create academic year aggregate successfully`() {
        val year = AcademicYear.create(
            tenantId = tenantId,
            name = "2026/2027",
            startDate = LocalDate.of(2026, 7, 1),
            endDate = LocalDate.of(2027, 6, 30)
        )

        assertThat(year.name).isEqualTo("2026/2027")
        assertThat(year.status).isEqualTo(AcademicYearStatus.PLANNED)
        assertThat(year.terms).isEmpty()

        val events = year.pullDomainEvents()
        assertThat(events).hasSize(1)
        assertThat(events.first()).isInstanceOf(AcademicYearCreatedEvent::class.java)
    }

    @Test
    fun `should throw exception when start date is not before end date`() {
        assertThatThrownBy {
            AcademicYear.create(
                tenantId = tenantId,
                name = "2026/2027",
                startDate = LocalDate.of(2027, 7, 1),
                endDate = LocalDate.of(2026, 6, 30)
            )
        }.isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("must be strictly before end date")
    }

    @Test
    fun `should add valid terms and activate them`() {
        val year = AcademicYear.create(
            tenantId = tenantId,
            name = "2026/2027",
            startDate = LocalDate.of(2026, 7, 1),
            endDate = LocalDate.of(2027, 6, 30)
        )

        val term1 = AcademicTerm(
            id = AcademicTermId.generate(),
            name = "Semester 1",
            type = AcademicTermType.SEMESTER_1,
            displayName = "Semester Ganjil",
            startDate = LocalDate.of(2026, 7, 15),
            endDate = LocalDate.of(2026, 12, 20)
        )

        val term2 = AcademicTerm(
            id = AcademicTermId.generate(),
            name = "Semester 2",
            type = AcademicTermType.SEMESTER_2,
            displayName = "Semester Genap",
            startDate = LocalDate.of(2027, 1, 5),
            endDate = LocalDate.of(2027, 6, 15)
        )

        val withTerm1 = year.addTerm(term1)
        val withTerm2 = withTerm1.addTerm(term2)

        assertThat(withTerm2.terms).hasSize(2)

        val activatedTerm1 = withTerm2.activateTerm(term1.id)
        assertThat(activatedTerm1.terms.first { it.id == term1.id }.status).isEqualTo(AcademicTermStatus.ACTIVE)

        // Activating term 2 should archive term 1
        val activatedTerm2 = activatedTerm1.activateTerm(term2.id)
        assertThat(activatedTerm2.terms.first { it.id == term2.id }.status).isEqualTo(AcademicTermStatus.ACTIVE)
        assertThat(activatedTerm2.terms.first { it.id == term1.id }.status).isEqualTo(AcademicTermStatus.ARCHIVED)
    }

    @Test
    fun `should throw exception when term dates fall outside academic year`() {
        val year = AcademicYear.create(
            tenantId = tenantId,
            name = "2026/2027",
            startDate = LocalDate.of(2026, 7, 1),
            endDate = LocalDate.of(2027, 6, 30)
        )

        val outOfBoundsTerm = AcademicTerm(
            id = AcademicTermId.generate(),
            name = "Semester 1",
            type = AcademicTermType.SEMESTER_1,
            displayName = "Semester Ganjil",
            startDate = LocalDate.of(2026, 6, 15), // before July 1
            endDate = LocalDate.of(2026, 12, 20)
        )

        assertThatThrownBy { year.addTerm(outOfBoundsTerm) }
            .isInstanceOf(InvalidAcademicYearDatesException::class.java)
            .hasMessageContaining("must fall within the academic year range")
    }

    @Test
    fun `should throw exception when terms overlap in dates`() {
        val year = AcademicYear.create(
            tenantId = tenantId,
            name = "2026/2027",
            startDate = LocalDate.of(2026, 7, 1),
            endDate = LocalDate.of(2027, 6, 30)
        )

        val term1 = AcademicTerm(
            id = AcademicTermId.generate(),
            name = "Term 1",
            type = AcademicTermType.TRIMESTER_1,
            displayName = "Term 1",
            startDate = LocalDate.of(2026, 7, 15),
            endDate = LocalDate.of(2026, 10, 30)
        )

        val overlappingTerm = AcademicTerm(
            id = AcademicTermId.generate(),
            name = "Term 2",
            type = AcademicTermType.TRIMESTER_2,
            displayName = "Term 2",
            startDate = LocalDate.of(2026, 10, 15), // overlaps with Term 1
            endDate = LocalDate.of(2027, 1, 30)
        )

        val withTerm1 = year.addTerm(term1)

        assertThatThrownBy { withTerm1.addTerm(overlappingTerm) }
            .isInstanceOf(InvalidAcademicYearDatesException::class.java)
            .hasMessageContaining("overlaps with an existing term")
    }
}
