package io.github.edmaputra.ilmiya.common.domain

/**
 * Marker contract for entities or aggregates scoped to a specific tenant.
 *
 * @author edmaputra
 * @since 0.0.1
 */
interface TenantAware {
    val tenantId: TenantId
}
