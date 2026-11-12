package io.github.edmaputra.ilmiya.app.tenant

import io.github.edmaputra.ilmiya.common.domain.TenantId
import io.github.edmaputra.ilmiya.common.dto.PageResponse
import io.github.edmaputra.ilmiya.core.tenant.application.ListTenantsService
import io.github.edmaputra.ilmiya.core.tenant.application.ManageTenantService
import io.github.edmaputra.ilmiya.core.tenant.domain.Tenant
import io.github.edmaputra.ilmiya.core.tenant.domain.port.`in`.CreateTenantCommand
import io.github.edmaputra.ilmiya.core.tenant.domain.port.`in`.CreateTenantUseCase
import io.github.edmaputra.ilmiya.core.tenant.domain.port.`in`.DeleteTenantUseCase
import io.github.edmaputra.ilmiya.core.tenant.domain.port.`in`.GetTenantUseCase
import io.github.edmaputra.ilmiya.core.tenant.domain.port.`in`.ListTenantsUseCase
import io.github.edmaputra.ilmiya.core.tenant.domain.port.`in`.UpdateTenantCommand
import io.github.edmaputra.ilmiya.core.tenant.domain.port.`in`.UpdateTenantUseCase
import io.github.edmaputra.ilmiya.core.tenant.domain.port.out.TenantEventPublisher
import io.github.edmaputra.ilmiya.core.tenant.domain.port.out.TenantRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Transactional decorator for tenant foundation CRUD management use cases.
 *
 * @author edmaputra
 * @since 0.0.1
 */
@Service
class TransactionalManageTenantUseCase(
    tenantRepository: TenantRepository,
    eventPublisher: TenantEventPublisher
) : CreateTenantUseCase, GetTenantUseCase, UpdateTenantUseCase, DeleteTenantUseCase {

    private val delegate = ManageTenantService(tenantRepository, eventPublisher)

    @Transactional
    override fun createTenant(command: CreateTenantCommand): Tenant =
        delegate.createTenant(command)

    @Transactional(readOnly = true)
    override fun getTenant(tenantId: TenantId): Tenant =
        delegate.getTenant(tenantId)

    @Transactional
    override fun updateTenant(tenantId: TenantId, command: UpdateTenantCommand): Tenant =
        delegate.updateTenant(tenantId, command)

    @Transactional
    override fun deleteTenant(tenantId: TenantId) =
        delegate.deleteTenant(tenantId)
}

/**
 * Transactional decorator for tenant listing use case.
 *
 * @author edmaputra
 * @since 0.0.1
 */
@Service
class TransactionalListTenantsUseCase(
    tenantRepository: TenantRepository
) : ListTenantsUseCase {

    private val delegate = ListTenantsService(tenantRepository)

    @Transactional(readOnly = true)
    override fun listTenants(page: Int, size: Int): PageResponse<Tenant> =
        delegate.listTenants(page, size)
}
