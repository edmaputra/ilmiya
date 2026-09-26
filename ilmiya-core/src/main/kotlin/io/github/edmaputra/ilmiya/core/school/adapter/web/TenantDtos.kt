package io.github.edmaputra.ilmiya.core.school.adapter.web

import io.github.edmaputra.ilmiya.core.school.domain.Accreditation
import io.github.edmaputra.ilmiya.core.school.domain.GradingPreference
import io.github.edmaputra.ilmiya.core.school.domain.OperationalSettings
import io.github.edmaputra.ilmiya.core.school.domain.School
import io.github.edmaputra.ilmiya.core.school.domain.SchoolStatus
import io.github.edmaputra.ilmiya.core.school.domain.TermLayout
import io.github.edmaputra.ilmiya.core.school.domain.port.`in`.CreateTenantCommand
import io.github.edmaputra.ilmiya.core.school.domain.port.`in`.UpdateTenantCommand
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import java.time.DayOfWeek
import java.time.LocalDate

/**
 * Request payload for creating and provisioning a new tenant.
 *
 * @author edmaputra
 * @since 0.0.1
 */
data class CreateTenantRequest(
    @field:NotBlank(message = "Tenant ID is required")
    val tenantId: String,

    @field:NotBlank(message = "School/Tenant name is required")
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
    fun toCommand(): CreateTenantCommand = CreateTenantCommand(
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
 * Request payload for updating an existing tenant.
 *
 * @author edmaputra
 * @since 0.0.1
 */
data class UpdateTenantRequest(
    @field:NotBlank(message = "School/Tenant name is required")
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
    val accreditationValidUntil: LocalDate? = null,
    val status: SchoolStatus = SchoolStatus.ACTIVE
) {
    fun toCommand(): UpdateTenantCommand = UpdateTenantCommand(
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
        ),
        status = status
    )
}

/**
 * Response representation of a tenant summary.
 *
 * @author edmaputra
 * @since 0.0.1
 */
data class TenantResponse(
    val id: String,
    val tenantId: String,
    val name: String,
    val institutionCode: String,
    val status: String,
    val logoUrl: String? = null,
    val address: String? = null,
    val contactEmail: String? = null,
    val contactPhone: String? = null,
    val governmentRegistrationNumber: String? = null,
    val registrationType: String? = null,
    val createdAt: String,
    val updatedAt: String
) {
    companion object {
        fun from(school: School): TenantResponse = TenantResponse(
            id = school.id.value,
            tenantId = school.tenantId.value,
            name = school.name,
            institutionCode = school.institutionCode,
            status = school.status.name,
            logoUrl = school.logoUrl,
            address = school.address,
            contactEmail = school.contactEmail,
            contactPhone = school.contactPhone,
            governmentRegistrationNumber = school.governmentRegistrationNumber,
            registrationType = school.registrationType,
            createdAt = school.createdAt.toString(),
            updatedAt = school.updatedAt.toString()
        )
    }
}
