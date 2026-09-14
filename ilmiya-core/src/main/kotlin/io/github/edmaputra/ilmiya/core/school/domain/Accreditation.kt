package io.github.edmaputra.ilmiya.core.school.domain

import java.time.LocalDate

/**
 * Represents accreditation information for an educational institution.
 *
 * @property status the accreditation rating or grade (e.g., "A", "B", "ACCREDITED", "PENDING")
 * @property accreditingBody the name of the granting organization (e.g., "BAN-S/M", "Cambridge", "Cognia")
 * @property validUntil the expiration date of current accreditation status
 * @author edmaputra
 */
data class Accreditation(
    val status: String,
    val accreditingBody: String? = null,
    val validUntil: LocalDate? = null
) {
    init {
        require(status.isNotBlank()) { "Accreditation status must not be blank." }
    }

    companion object {
        val UNACCREDITED = Accreditation(status = "UNACCREDITED")
    }
}
