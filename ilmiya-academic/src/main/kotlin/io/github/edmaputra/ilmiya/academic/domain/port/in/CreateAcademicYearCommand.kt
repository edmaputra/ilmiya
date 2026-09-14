package io.github.edmaputra.ilmiya.academic.domain.port.`in`

import java.time.LocalDate

/**
 * Command containing payload to create a new academic year.
 *
 * @property name academic year name (e.g., "2026/2027")
 * @property startDate start date of the academic year
 * @property endDate end date of the academic year
 * @author edmaputra
 */
data class CreateAcademicYearCommand(
    val name: String,
    val startDate: LocalDate,
    val endDate: LocalDate
) {
    init {
        require(name.isNotBlank()) { "Academic year name must not be blank." }
        require(startDate.isBefore(endDate)) {
            "Academic year start date ($startDate) must be before end date ($endDate)."
        }
    }
}
