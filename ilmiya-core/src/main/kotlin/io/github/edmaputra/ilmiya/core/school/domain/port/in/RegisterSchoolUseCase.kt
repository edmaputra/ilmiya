package io.github.edmaputra.ilmiya.core.school.domain.port.`in`

import io.github.edmaputra.ilmiya.core.school.domain.School

/**
 * Inbound port for provisioning a new school institution and initializing tenant boundaries.
 *
 * @author edmaputra
 */
interface RegisterSchoolUseCase {

    /**
     * Provisions a new school with configured parameters and initializes its tenant domain boundary.
     *
     * @param command input payload for school registration
     * @return the registered school aggregate root
     * @throws io.github.edmaputra.ilmiya.core.school.domain.exception.DuplicateInstitutionCodeException if institutionCode is in use
     */
    fun register(command: RegisterSchoolCommand): School
}
