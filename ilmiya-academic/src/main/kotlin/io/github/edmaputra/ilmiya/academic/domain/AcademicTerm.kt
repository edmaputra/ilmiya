package io.github.edmaputra.ilmiya.academic.domain

import java.time.LocalDate

/**
 * Entity representing an academic term/semester belonging to an academic year.
 *
 * @property id unique identifier of the term
 * @property name system or standard term name
 * @property type universal term classification
 * @property displayName localized institutional display name (e.g., "Semester Ganjil", "Fall 2026")
 * @property startDate inclusive start date of the term
 * @property endDate inclusive end date of the term
 * @property status current operational status of the term
 * @author edmaputra
 */
data class AcademicTerm(
    val id: AcademicTermId,
    val name: String,
    val type: AcademicTermType,
    val displayName: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val status: AcademicTermStatus = AcademicTermStatus.PLANNED
) {
    init {
        require(name.isNotBlank()) { "Academic term name must not be blank." }
        require(displayName.isNotBlank()) { "Academic term display name must not be blank." }
        require(startDate.isBefore(endDate)) {
            "Academic term start date ($startDate) must be before end date ($endDate)."
        }
    }

    fun activate(): AcademicTerm = copy(status = AcademicTermStatus.ACTIVE)

    fun archive(): AcademicTerm = copy(status = AcademicTermStatus.ARCHIVED)
}
