package io.github.edmaputra.ilmiya.core.school.domain.port.`in`

import io.github.edmaputra.ilmiya.core.school.domain.Accreditation
import io.github.edmaputra.ilmiya.core.school.domain.SchoolStatus

/**
 * Domain command carrying updated tenant profile details.
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
    val governmentRegistrationNumber: String? = null,
    val registrationType: String? = null,
    val accreditation: Accreditation = Accreditation.UNACCREDITED,
    val status: SchoolStatus = SchoolStatus.ACTIVE
)
