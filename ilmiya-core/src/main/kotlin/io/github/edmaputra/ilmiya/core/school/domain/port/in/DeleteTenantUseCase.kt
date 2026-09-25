package io.github.edmaputra.ilmiya.core.school.domain.port.`in`

import io.github.edmaputra.ilmiya.common.domain.TenantId

/**
 * Inbound port contract for deleting a tenant.
 *
 * @author edmaputra
 * @since 0.0.1
 */
interface DeleteTenantUseCase {

    /**
     * Deletes a tenant by its identifier.
     *
     * @param tenantId tenant identifier
     */
    fun deleteTenant(tenantId: TenantId)
}
