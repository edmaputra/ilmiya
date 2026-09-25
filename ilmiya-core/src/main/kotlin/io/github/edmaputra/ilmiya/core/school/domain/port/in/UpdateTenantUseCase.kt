package io.github.edmaputra.ilmiya.core.school.domain.port.`in`

import io.github.edmaputra.ilmiya.common.domain.TenantId
import io.github.edmaputra.ilmiya.core.school.domain.School

/**
 * Inbound port contract for updating an existing tenant profile.
 *
 * @author edmaputra
 * @since 0.0.1
 */
interface UpdateTenantUseCase {

    /**
     * Updates tenant details for the specified tenant identifier.
     *
     * @param tenantId tenant identifier
     * @param command updated profile parameters
     * @return updated school aggregate
     */
    fun updateTenant(tenantId: TenantId, command: UpdateTenantCommand): School
}
