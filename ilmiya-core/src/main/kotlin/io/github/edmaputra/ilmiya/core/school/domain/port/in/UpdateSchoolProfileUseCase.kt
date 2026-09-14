package io.github.edmaputra.ilmiya.core.school.domain.port.`in`

import io.github.edmaputra.ilmiya.common.domain.TenantId
import io.github.edmaputra.ilmiya.core.school.domain.School

/**
 * Inbound port for updating institutional profile details.
 *
 * @author edmaputra
 */
interface UpdateSchoolProfileUseCase {

    /**
     * Updates the school profile details for the specified tenant.
     *
     * @param tenantId tenant identifier whose profile is being updated
     * @param command updated profile parameters
     * @return the updated school aggregate
     * @throws io.github.edmaputra.ilmiya.core.school.domain.exception.SchoolNotFoundException if no school exists for the tenant
     */
    fun updateProfile(tenantId: TenantId, command: UpdateSchoolProfileCommand): School
}
