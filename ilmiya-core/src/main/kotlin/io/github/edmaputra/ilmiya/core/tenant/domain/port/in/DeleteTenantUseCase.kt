package io.github.edmaputra.ilmiya.core.tenant.domain.port.`in`

import io.github.edmaputra.ilmiya.common.domain.TenantId

/**
 * Inbound port contract for deleting a tenant foundation.
 *
 * @author edmaputra
 * @since 0.0.1
 */
interface DeleteTenantUseCase {
    fun deleteTenant(tenantId: TenantId)
}
