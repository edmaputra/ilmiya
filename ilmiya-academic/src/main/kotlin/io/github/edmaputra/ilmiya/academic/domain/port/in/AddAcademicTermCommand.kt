package io.github.edmaputra.ilmiya.academic.domain.port.`in`

import io.github.edmaputra.ilmiya.academic.domain.AcademicTermType
import java.time.LocalDate

/**
 * Command containing payload to define and attach an academic term/semester to an academic year.
 *
 * @property name system term name
 * @property type universal term classification
 * @property displayName localized institutional display name
 * @property startDate start date of the term
 * @property endDate end date of the term
 * @author edmaputra
 */
data class AddAcademicTermCommand(
    val name: String,
    val type: AcademicTermType,
    val displayName: String,
    val startDate: LocalDate,
    val endDate: LocalDate
) {
    init {
        require(name.isNotBlank()) { "Term name must not be blank." }
        require(displayName.isNotBlank()) { "Term display name must not be blank." }
        require(startDate.isBefore(endDate)) {
            "Term start date ($startDate) must be before end date ($endDate)."
        }
    }
}
