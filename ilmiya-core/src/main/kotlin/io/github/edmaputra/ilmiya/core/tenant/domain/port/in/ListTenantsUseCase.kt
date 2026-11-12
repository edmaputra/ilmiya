package io.github.edmaputra.ilmiya.core.tenant.domain.port.`in`

import io.github.edmaputra.ilmiya.common.dto.PageResponse
import io.github.edmaputra.ilmiya.core.tenant.domain.Tenant

/**
 * Inbound port contract for listing registered tenant foundations with pagination.
 *
 * @author edmaputra
 * @since 0.0.1
 */
interface ListTenantsUseCase {
    fun listTenants(page: Int, size: Int): PageResponse<Tenant>
}
