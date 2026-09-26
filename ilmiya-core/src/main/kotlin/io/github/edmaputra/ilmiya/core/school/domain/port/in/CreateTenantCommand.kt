package io.github.edmaputra.ilmiya.core.school.domain.port.`in`

import io.github.edmaputra.ilmiya.core.school.domain.Accreditation
import io.github.edmaputra.ilmiya.core.school.domain.OperationalSettings

/**
 * Domain command carrying attributes required to provision a new tenant boundary.
 *
 * @author edmaputra
 * @since 0.0.1
 */
data class CreateTenantCommand(
    val tenantId: String,
    val name: String,
    val institutionCode: String,
    val governmentRegistrationNumber: String? = null,
    val registrationType: String? = null,
    val address: String,
    val contactEmail: String,
    val contactPhone: String,
    val logoUrl: String? = null,
    val accreditation: Accreditation = Accreditation.UNACCREDITED,
    val operationalSettings: OperationalSettings = OperationalSettings()
)
