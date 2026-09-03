package io.github.edmaputra.ilmiya.common.domain

/**
 * Marker contract for entities or aggregates scoped to a specific tenant.
 *
 * @author edmaputra
 */
interface TenantAware {
    val tenantId: TenantId
}
