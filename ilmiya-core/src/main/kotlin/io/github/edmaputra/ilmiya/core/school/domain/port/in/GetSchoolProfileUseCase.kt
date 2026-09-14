package io.github.edmaputra.ilmiya.core.school.domain.port.`in`

import io.github.edmaputra.ilmiya.common.domain.TenantId
import io.github.edmaputra.ilmiya.core.school.domain.School

/**
 * Inbound port for retrieving the current institutional profile within the active tenant boundary.
 *
 * @author edmaputra
 */
interface GetSchoolProfileUseCase {

    /**
     * Retrieves the school profile associated with the specified tenant.
     *
     * @param tenantId tenant identifier whose profile is requested
     * @return the school aggregate
     * @throws io.github.edmaputra.ilmiya.core.school.domain.exception.SchoolNotFoundException if no school exists for the tenant
     */
    fun getProfile(tenantId: TenantId): School
}
