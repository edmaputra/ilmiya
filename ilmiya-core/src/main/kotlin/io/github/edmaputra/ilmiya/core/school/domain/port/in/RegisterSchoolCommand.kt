package io.github.edmaputra.ilmiya.core.school.domain.port.`in`

import io.github.edmaputra.ilmiya.core.school.domain.Accreditation
import io.github.edmaputra.ilmiya.core.school.domain.OperationalSettings

/**
 * Command containing payload required to register and provision a new school institution.
 *
 * @property tenantId requested or generated tenant boundary key
 * @property name official school institution name
 * @property institutionCode unique short code across the platform
 * @property governmentRegistrationNumber optional official national registration number (e.g. NPSN)
 * @property registrationType type or jurisdiction of registration (e.g., "NPSN", "NCES")
 * @property address physical campus address
 * @property contactEmail administrative contact email
 * @property contactPhone administrative contact telephone number
 * @property logoUrl optional URI of school emblem or logo
 * @property accreditation initial accreditation status
 * @property operationalSettings initial operational configuration
 * @author edmaputra
 * @since 0.0.1
 */
data class RegisterSchoolCommand(
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
) {
    init {
        require(tenantId.isNotBlank()) { "Tenant ID must not be blank." }
        require(name.isNotBlank()) { "School name must not be blank." }
        require(institutionCode.isNotBlank()) { "Institution code must not be blank." }
        require(address.isNotBlank()) { "School address must not be blank." }
        require(contactEmail.isNotBlank()) { "Contact email must not be blank." }
        require(contactPhone.isNotBlank()) { "Contact phone must not be blank." }
    }
}
