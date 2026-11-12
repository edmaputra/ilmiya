package io.github.edmaputra.ilmiya.core.tenant.domain.port.out

import io.github.edmaputra.ilmiya.common.domain.TenantId
import io.github.edmaputra.ilmiya.core.tenant.domain.Tenant

/**
 * Outbound repository port for persisting and querying [Tenant] foundation aggregates.
 *
 * @author edmaputra
 * @since 0.0.1
 */
interface TenantRepository {

    /**
     * Saves a tenant foundation entity.
     */
    fun save(tenant: Tenant): Tenant

    /**
     * Finds a tenant by its unique identifier.
     */
    fun findById(id: TenantId): Tenant?

    /**
     * Checks if a tenant exists with the given identifier.
     */
    fun existsById(id: TenantId): Boolean

    /**
     * Retrieves a paginated list of tenants.
     */
    fun findAll(page: Int, size: Int): List<Tenant>

    /**
     * Counts the total number of registered tenants.
     */
    fun count(): Long

    /**
     * Deletes a tenant by its identifier.
     */
    fun deleteById(id: TenantId)
}
