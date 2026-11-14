package io.github.edmaputra.ilmiya.core.tenant.domain.port.`in`

import io.github.edmaputra.ilmiya.core.tenant.domain.TenantStatus

/**
 * Command for updating an existing tenant foundation profile.
 *
 * @author edmaputra
 * @since 0.0.1
 */
data class UpdateTenantCommand(
    val name: String,
    val address: String,
    val contactEmail: String,
    val contactPhone: String,
    val logoUrl: String? = null,
    val legalEntityNumber: String? = null,
    val registrationType: String? = null,
    val status: TenantStatus = TenantStatus.ACTIVE
)
