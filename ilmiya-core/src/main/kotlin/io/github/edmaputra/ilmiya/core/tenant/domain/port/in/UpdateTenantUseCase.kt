package io.github.edmaputra.ilmiya.core.tenant.domain.port.`in`

import io.github.edmaputra.ilmiya.common.domain.TenantId
import io.github.edmaputra.ilmiya.core.tenant.domain.Tenant

/**
 * Inbound port contract for updating a tenant foundation.
 *
 * @author edmaputra
 * @since 0.0.1
 */
interface UpdateTenantUseCase {
    fun updateTenant(tenantId: TenantId, command: UpdateTenantCommand): Tenant
}
