package io.github.edmaputra.ilmiya.core.tenant.domain.port.`in`

import io.github.edmaputra.ilmiya.common.domain.TenantId
import io.github.edmaputra.ilmiya.core.tenant.domain.Tenant

/**
 * Inbound port contract for retrieving a tenant foundation by ID.
 *
 * @author edmaputra
 * @since 0.0.1
 */
interface GetTenantUseCase {
    fun getTenant(tenantId: TenantId): Tenant
}
