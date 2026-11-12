package io.github.edmaputra.ilmiya.core.tenant.domain.port.`in`

import io.github.edmaputra.ilmiya.core.tenant.domain.Tenant

/**
 * Inbound port contract for provisioning a new tenant foundation.
 *
 * @author edmaputra
 * @since 0.0.1
 */
interface CreateTenantUseCase {
    fun createTenant(command: CreateTenantCommand): Tenant
}
