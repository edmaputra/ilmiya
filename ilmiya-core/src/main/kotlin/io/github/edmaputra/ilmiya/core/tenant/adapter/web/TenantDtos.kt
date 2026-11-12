package io.github.edmaputra.ilmiya.core.tenant.adapter.web

import io.github.edmaputra.ilmiya.core.tenant.domain.Tenant
import io.github.edmaputra.ilmiya.core.tenant.domain.TenantStatus
import io.github.edmaputra.ilmiya.core.tenant.domain.port.`in`.CreateTenantCommand
import io.github.edmaputra.ilmiya.core.tenant.domain.port.`in`.UpdateTenantCommand
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

/**
 * Request payload for creating and provisioning a new tenant foundation.
 *
 * @author edmaputra
 * @since 0.0.1
 */
data class CreateTenantRequest(
    @field:NotBlank(message = "Tenant ID is required")
    val tenantId: String,

    @field:NotBlank(message = "Tenant/Foundation name is required")
    val name: String,

    val legalEntityNumber: String? = null,
    val registrationType: String? = null,

    @field:NotBlank(message = "Address is required")
    val address: String,

    @field:NotBlank(message = "Contact email is required")
    @field:Email(message = "Valid contact email is required")
    val contactEmail: String,

    @field:NotBlank(message = "Contact phone is required")
    val contactPhone: String,

    val logoUrl: String? = null
) {
    fun toCommand(): CreateTenantCommand = CreateTenantCommand(
        tenantId = tenantId,
        name = name,
        legalEntityNumber = legalEntityNumber,
        registrationType = registrationType,
        address = address,
        contactEmail = contactEmail,
        contactPhone = contactPhone,
        logoUrl = logoUrl
    )
}

/**
 * Request payload for updating an existing tenant foundation.
 *
 * @author edmaputra
 * @since 0.0.1
 */
data class UpdateTenantRequest(
    @field:NotBlank(message = "Tenant/Foundation name is required")
    val name: String,

    @field:NotBlank(message = "Address is required")
    val address: String,

    @field:NotBlank(message = "Contact email is required")
    @field:Email(message = "Valid contact email is required")
    val contactEmail: String,

    @field:NotBlank(message = "Contact phone is required")
    val contactPhone: String,

    val logoUrl: String? = null,
    val legalEntityNumber: String? = null,
    val registrationType: String? = null,
    val status: TenantStatus = TenantStatus.ACTIVE
) {
    fun toCommand(): UpdateTenantCommand = UpdateTenantCommand(
        name = name,
        address = address,
        contactEmail = contactEmail,
        contactPhone = contactPhone,
        logoUrl = logoUrl,
        legalEntityNumber = legalEntityNumber,
        registrationType = registrationType,
        status = status
    )
}

/**
 * Response representation of a tenant foundation summary.
 *
 * @author edmaputra
 * @since 0.0.1
 */
data class TenantResponse(
    val id: String,
    val tenantId: String,
    val name: String,
    val legalEntityNumber: String? = null,
    val registrationType: String? = null,
    val address: String,
    val contactEmail: String,
    val contactPhone: String,
    val logoUrl: String? = null,
    val status: String,
    val createdAt: String,
    val updatedAt: String
) {
    companion object {
        fun from(tenant: Tenant): TenantResponse = TenantResponse(
            id = tenant.id.value,
            tenantId = tenant.id.value,
            name = tenant.name,
            legalEntityNumber = tenant.legalEntityNumber,
            registrationType = tenant.registrationType,
            address = tenant.address,
            contactEmail = tenant.contactEmail,
            contactPhone = tenant.contactPhone,
            logoUrl = tenant.logoUrl,
            status = tenant.status.name,
            createdAt = tenant.createdAt.toString(),
            updatedAt = tenant.updatedAt.toString()
        )
    }
}
