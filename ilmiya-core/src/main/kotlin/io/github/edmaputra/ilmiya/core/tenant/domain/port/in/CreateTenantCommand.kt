package io.github.edmaputra.ilmiya.core.tenant.domain.port.`in`

/**
 * Command for provisioning a new tenant foundation.
 *
 * @author edmaputra
 * @since 0.0.1
 */
data class CreateTenantCommand(
    val tenantId: String,
    val name: String,
    val legalEntityNumber: String? = null,
    val registrationType: String? = null,
    val address: String,
    val contactEmail: String,
    val contactPhone: String,
    val logoUrl: String? = null
)
