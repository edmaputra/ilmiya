package io.github.edmaputra.ilmiya.core.school.domain.port.`in`

import io.github.edmaputra.ilmiya.common.domain.TenantId
import io.github.edmaputra.ilmiya.core.school.domain.School

/**
 * Inbound port contract for retrieving a tenant by identifier.
 *
 * @author edmaputra
 * @since 0.0.1
 */
interface GetTenantUseCase {

    /**
     * Retrieves tenant and school details for the given tenant identifier.
     *
     * @param tenantId tenant identifier
     * @return school aggregate representing the tenant
     */
    fun getTenant(tenantId: TenantId): School
}
