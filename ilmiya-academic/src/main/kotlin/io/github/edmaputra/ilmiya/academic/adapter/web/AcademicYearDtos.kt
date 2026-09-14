package io.github.edmaputra.ilmiya.academic.adapter.web

import io.github.edmaputra.ilmiya.academic.domain.AcademicTerm
import io.github.edmaputra.ilmiya.academic.domain.AcademicTermType
import io.github.edmaputra.ilmiya.academic.domain.AcademicYear
import io.github.edmaputra.ilmiya.academic.domain.port.`in`.AddAcademicTermCommand
import io.github.edmaputra.ilmiya.academic.domain.port.`in`.CreateAcademicYearCommand
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDate

/**
 * Request payload for creating a new academic year.
 *
 * @author edmaputra
 */
data class CreateAcademicYearRequest(
    @field:NotBlank(message = "Academic year name is required")
    val name: String,

    @field:NotNull(message = "Start date is required")
    val startDate: LocalDate,

    @field:NotNull(message = "End date is required")
    val endDate: LocalDate
) {
    fun toCommand(): CreateAcademicYearCommand = CreateAcademicYearCommand(
        name = name,
        startDate = startDate,
        endDate = endDate
    )
}

/**
 * Request payload for adding an academic term to an academic year.
 *
 * @author edmaputra
 */
data class AddAcademicTermRequest(
    @field:NotBlank(message = "Term name is required")
    val name: String,

    @field:NotNull(message = "Term type is required")
    val type: AcademicTermType,

    @field:NotBlank(message = "Display name is required")
    val displayName: String,

    @field:NotNull(message = "Start date is required")
    val startDate: LocalDate,

    @field:NotNull(message = "End date is required")
    val endDate: LocalDate
) {
    fun toCommand(): AddAcademicTermCommand = AddAcademicTermCommand(
        name = name,
        type = type,
        displayName = displayName,
        startDate = startDate,
        endDate = endDate
    )
}

/**
 * Response payload representing an academic term.
 *
 * @author edmaputra
 */
data class AcademicTermResponse(
    val id: String,
    val name: String,
    val type: AcademicTermType,
    val displayName: String,
    val startDate: String,
    val endDate: String,
    val status: String
) {
    companion object {
        fun from(term: AcademicTerm): AcademicTermResponse = AcademicTermResponse(
            id = term.id.value,
            name = term.name,
            type = term.type,
            displayName = term.displayName,
            startDate = term.startDate.toString(),
            endDate = term.endDate.toString(),
            status = term.status.name
        )
    }
}

/**
 * Response payload representing an academic year and its terms.
 *
 * @author edmaputra
 */
data class AcademicYearResponse(
    val id: String,
    val tenantId: String,
    val name: String,
    val startDate: String,
    val endDate: String,
    val status: String,
    val terms: List<AcademicTermResponse>,
    val createdAt: String,
    val updatedAt: String
) {
    companion object {
        fun from(year: AcademicYear): AcademicYearResponse = AcademicYearResponse(
            id = year.id.value,
            tenantId = year.tenantId.value,
            name = year.name,
            startDate = year.startDate.toString(),
            endDate = year.endDate.toString(),
            status = year.status.name,
            terms = year.terms.map { AcademicTermResponse.from(it) },
            createdAt = year.createdAt.toString(),
            updatedAt = year.updatedAt.toString()
        )
    }
}
