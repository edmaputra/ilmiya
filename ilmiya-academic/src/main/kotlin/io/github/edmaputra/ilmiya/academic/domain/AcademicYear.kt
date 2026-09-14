package io.github.edmaputra.ilmiya.academic.domain

import io.github.edmaputra.ilmiya.academic.domain.exception.AcademicTermNotFoundException
import io.github.edmaputra.ilmiya.academic.domain.exception.InvalidAcademicYearDatesException
import io.github.edmaputra.ilmiya.common.domain.AggregateRoot
import io.github.edmaputra.ilmiya.common.domain.Auditable
import io.github.edmaputra.ilmiya.common.domain.TenantAware
import io.github.edmaputra.ilmiya.common.domain.TenantId
import java.time.Instant
import java.time.LocalDate

/**
 * Aggregate root representing an institutional academic calendar year.
 * Manages terms/semesters, date validations, and academic lifecycle status transitions.
 *
 * @property id unique identifier of the academic year
 * @property tenantId tenant boundary discriminator
 * @property name academic year label (e.g., "2026/2027")
 * @property startDate official start date of the academic year
 * @property endDate official end date of the academic year
 * @property status operational lifecycle status of the year
 * @property terms list of child academic terms
 * @property createdAt timestamp of creation
 * @property updatedAt timestamp of latest modification
 * @author edmaputra
 */
data class AcademicYear(
    override val id: AcademicYearId,
    override val tenantId: TenantId,
    val name: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val status: AcademicYearStatus = AcademicYearStatus.PLANNED,
    val terms: List<AcademicTerm> = emptyList(),
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now()
) : AggregateRoot<AcademicYearId>(), TenantAware, Auditable {

    init {
        require(name.isNotBlank()) { "Academic year name must not be blank." }
        require(startDate.isBefore(endDate)) {
            "Academic year start date ($startDate) must be strictly before end date ($endDate)."
        }
    }

    /**
     * Adds an academic term, validating date boundaries and checking for overlap.
     */
    fun addTerm(term: AcademicTerm): AcademicYear {
        if (term.startDate.isBefore(startDate) || term.endDate.isAfter(endDate)) {
            throw InvalidAcademicYearDatesException(
                "Term '${term.displayName}' date range (${term.startDate} to ${term.endDate}) " +
                    "must fall within the academic year range ($startDate to $endDate)."
            )
        }

        val hasOverlap = terms.any { existing ->
            term.startDate.isBefore(existing.endDate) && term.endDate.isAfter(existing.startDate)
        }
        if (hasOverlap) {
            throw InvalidAcademicYearDatesException(
                "Term '${term.displayName}' date range overlaps with an existing term in academic year '$name'."
            )
        }

        val updatedTerms = terms + term
        val updatedYear = copy(
            terms = updatedTerms,
            updatedAt = Instant.now()
        )
        updatedYear.registerEvent(
            AcademicTermAddedEvent(
                academicYearId = id,
                termId = term.id,
                tenantId = tenantId
            )
        )
        return updatedYear
    }

    /**
     * Activates this academic year and records the domain event.
     */
    fun activate(): AcademicYear {
        val updated = copy(
            status = AcademicYearStatus.ACTIVE,
            updatedAt = Instant.now()
        )
        updated.registerEvent(
            AcademicYearActivatedEvent(
                academicYearId = id,
                tenantId = tenantId
            )
        )
        return updated
    }

    /**
     * Archives this academic year and all of its terms.
     */
    fun archive(): AcademicYear {
        val archivedTerms = terms.map { it.archive() }
        return copy(
            status = AcademicYearStatus.ARCHIVED,
            terms = archivedTerms,
            updatedAt = Instant.now()
        )
    }

    /**
     * Activates a specific academic term while archiving previous active terms.
     */
    fun activateTerm(termId: AcademicTermId): AcademicYear {
        if (terms.none { it.id == termId }) {
            throw AcademicTermNotFoundException("Term with ID '${termId.value}' not found in academic year '$name'.")
        }

        val updatedTerms = terms.map { term ->
            if (term.id == termId) {
                term.activate()
            } else if (term.status == AcademicTermStatus.ACTIVE) {
                term.archive()
            } else {
                term
            }
        }

        return copy(
            terms = updatedTerms,
            updatedAt = Instant.now()
        )
    }

    override fun auditableFields(): Map<String, Any?> = mapOf(
        "academicYearId" to id.value,
        "tenantId" to tenantId.value,
        "name" to name,
        "startDate" to startDate.toString(),
        "endDate" to endDate.toString(),
        "status" to status.name,
        "termCount" to terms.size
    )

    companion object {
        fun create(
            id: AcademicYearId = AcademicYearId.generate(),
            tenantId: TenantId,
            name: String,
            startDate: LocalDate,
            endDate: LocalDate
        ): AcademicYear {
            val year = AcademicYear(
                id = id,
                tenantId = tenantId,
                name = name.trim(),
                startDate = startDate,
                endDate = endDate
            )
            year.registerEvent(
                AcademicYearCreatedEvent(
                    academicYearId = year.id,
                    tenantId = year.tenantId,
                    name = year.name
                )
            )
            return year
        }
    }
}
