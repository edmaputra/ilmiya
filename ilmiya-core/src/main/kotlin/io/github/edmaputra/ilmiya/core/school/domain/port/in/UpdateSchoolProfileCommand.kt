package io.github.edmaputra.ilmiya.core.school.domain.port.`in`

import io.github.edmaputra.ilmiya.core.school.domain.Accreditation

/**
 * Command containing payload to update an existing school profile.
 *
 * @property name updated school name
 * @property address updated campus address
 * @property contactEmail updated contact email
 * @property contactPhone updated contact phone
 * @property logoUrl updated emblem/logo URL
 * @property governmentRegistrationNumber updated national/state registration number
 * @property registrationType updated registration type
 * @property accreditation updated accreditation status
 * @author edmaputra
 * @since 0.0.1
 */
data class UpdateSchoolProfileCommand(
    val name: String,
    val address: String,
    val contactEmail: String,
    val contactPhone: String,
    val logoUrl: String? = null,
    val governmentRegistrationNumber: String? = null,
    val registrationType: String? = null,
    val accreditation: Accreditation = Accreditation.UNACCREDITED
) {
    init {
        require(name.isNotBlank()) { "School name must not be blank." }
        require(address.isNotBlank()) { "School address must not be blank." }
        require(contactEmail.isNotBlank()) { "Contact email must not be blank." }
        require(contactPhone.isNotBlank()) { "Contact phone must not be blank." }
    }
}
