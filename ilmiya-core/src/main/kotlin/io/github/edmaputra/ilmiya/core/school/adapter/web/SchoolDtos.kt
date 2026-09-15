package io.github.edmaputra.ilmiya.core.school.adapter.web

import io.github.edmaputra.ilmiya.core.school.domain.Accreditation
import io.github.edmaputra.ilmiya.core.school.domain.GradingPreference
import io.github.edmaputra.ilmiya.core.school.domain.OperationalSettings
import io.github.edmaputra.ilmiya.core.school.domain.School
import io.github.edmaputra.ilmiya.core.school.domain.TermLayout
import io.github.edmaputra.ilmiya.core.school.domain.port.`in`.ConfigureSchoolSettingsCommand
import io.github.edmaputra.ilmiya.core.school.domain.port.`in`.RegisterSchoolCommand
import io.github.edmaputra.ilmiya.core.school.domain.port.`in`.UpdateSchoolProfileCommand
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import java.time.DayOfWeek
import java.time.LocalDate

/**
 * Request payload for registering a new school and tenant.
 *
 * @author edmaputra
 * @since 0.0.1
 */
data class RegisterSchoolRequest(
    @field:NotBlank(message = "Tenant ID is required")
    val tenantId: String,

    @field:NotBlank(message = "School name is required")
    val name: String,

    @field:NotBlank(message = "Institution code is required")
    val institutionCode: String,

    val governmentRegistrationNumber: String? = null,
    val registrationType: String? = null,

    @field:NotBlank(message = "Address is required")
    val address: String,

    @field:NotBlank(message = "Contact email is required")
    @field:Email(message = "Valid contact email is required")
    val contactEmail: String,

    @field:NotBlank(message = "Contact phone is required")
    val contactPhone: String,

    val logoUrl: String? = null,
    val accreditationStatus: String = "UNACCREDITED",
    val accreditationBody: String? = null,
    val accreditationValidUntil: LocalDate? = null,
    val workingDays: Set<DayOfWeek>? = null,
    val gradingPreference: GradingPreference? = null,
    val termLayout: TermLayout? = null
) {
    fun toCommand(): RegisterSchoolCommand = RegisterSchoolCommand(
        tenantId = tenantId,
        name = name,
        institutionCode = institutionCode,
        governmentRegistrationNumber = governmentRegistrationNumber,
        registrationType = registrationType,
        address = address,
        contactEmail = contactEmail,
        contactPhone = contactPhone,
        logoUrl = logoUrl,
        accreditation = Accreditation(
            status = accreditationStatus,
            accreditingBody = accreditationBody,
            validUntil = accreditationValidUntil
        ),
        operationalSettings = OperationalSettings(
            workingDays = workingDays ?: setOf(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY),
            gradingPreference = gradingPreference ?: GradingPreference.NUMERIC_0_100,
            termLayout = termLayout ?: TermLayout.SEMESTER
        )
    )
}

/**
 * Request payload for updating an existing school profile.
 *
 * @author edmaputra
 * @since 0.0.1
 */
data class UpdateSchoolRequest(
    @field:NotBlank(message = "School name is required")
    val name: String,

    @field:NotBlank(message = "Address is required")
    val address: String,

    @field:NotBlank(message = "Contact email is required")
    @field:Email(message = "Valid contact email is required")
    val contactEmail: String,

    @field:NotBlank(message = "Contact phone is required")
    val contactPhone: String,

    val logoUrl: String? = null,
    val governmentRegistrationNumber: String? = null,
    val registrationType: String? = null,
    val accreditationStatus: String = "UNACCREDITED",
    val accreditationBody: String? = null,
    val accreditationValidUntil: LocalDate? = null
) {
    fun toCommand(): UpdateSchoolProfileCommand = UpdateSchoolProfileCommand(
        name = name,
        address = address,
        contactEmail = contactEmail,
        contactPhone = contactPhone,
        logoUrl = logoUrl,
        governmentRegistrationNumber = governmentRegistrationNumber,
        registrationType = registrationType,
        accreditation = Accreditation(
            status = accreditationStatus,
            accreditingBody = accreditationBody,
            validUntil = accreditationValidUntil
        )
    )
}

/**
 * Request payload for updating school operational settings.
 *
 * @author edmaputra
 * @since 0.0.1
 */
data class ConfigureSchoolSettingsRequest(
    @field:NotEmpty(message = "At least one working day must be specified")
    val workingDays: Set<DayOfWeek>,

    val gradingPreference: GradingPreference = GradingPreference.NUMERIC_0_100,
    val termLayout: TermLayout = TermLayout.SEMESTER
) {
    fun toCommand(): ConfigureSchoolSettingsCommand = ConfigureSchoolSettingsCommand(
        workingDays = workingDays,
        gradingPreference = gradingPreference,
        termLayout = termLayout
    )
}

/**
 * Response representation of a school institutional aggregate.
 *
 * @author edmaputra
 * @since 0.0.1
 */
data class SchoolResponse(
    val id: String,
    val tenantId: String,
    val name: String,
    val institutionCode: String,
    val governmentRegistrationNumber: String?,
    val registrationType: String?,
    val address: String,
    val contactEmail: String,
    val contactPhone: String,
    val logoUrl: String?,
    val accreditationStatus: String,
    val accreditationBody: String?,
    val accreditationValidUntil: LocalDate?,
    val workingDays: Set<DayOfWeek>,
    val gradingPreference: GradingPreference,
    val termLayout: TermLayout,
    val status: String,
    val createdAt: String,
    val updatedAt: String
) {
    companion object {
        fun from(school: School): SchoolResponse = SchoolResponse(
            id = school.id.value,
            tenantId = school.tenantId.value,
            name = school.name,
            institutionCode = school.institutionCode,
            governmentRegistrationNumber = school.governmentRegistrationNumber,
            registrationType = school.registrationType,
            address = school.address,
            contactEmail = school.contactEmail,
            contactPhone = school.contactPhone,
            logoUrl = school.logoUrl,
            accreditationStatus = school.accreditation.status,
            accreditationBody = school.accreditation.accreditingBody,
            accreditationValidUntil = school.accreditation.validUntil,
            workingDays = school.operationalSettings.workingDays,
            gradingPreference = school.operationalSettings.gradingPreference,
            termLayout = school.operationalSettings.termLayout,
            status = school.status.name,
            createdAt = school.createdAt.toString(),
            updatedAt = school.updatedAt.toString()
        )
    }
}
