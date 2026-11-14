package io.github.edmaputra.ilmiya.core.tenant.application

import io.github.edmaputra.ilmiya.common.dto.PageResponse
import io.github.edmaputra.ilmiya.core.tenant.domain.Tenant
import io.github.edmaputra.ilmiya.core.tenant.domain.port.`in`.ListTenantsUseCase
import io.github.edmaputra.ilmiya.core.tenant.domain.port.out.TenantRepository

/**
 * Application service for listing registered tenant foundations with pagination.
 *
 * @property tenantRepository outbound persistence port for tenant aggregates
 * @author edmaputra
 * @since 0.0.1
 */
class ListTenantsService(
    private val tenantRepository: TenantRepository
) : ListTenantsUseCase {

    override fun listTenants(page: Int, size: Int): PageResponse<Tenant> {
        val totalElements = tenantRepository.count()
        val content = tenantRepository.findAll(page, size)
        return PageResponse.of(
            content = content,
            page = page,
            size = size,
            totalElements = totalElements
        )
    }
}
