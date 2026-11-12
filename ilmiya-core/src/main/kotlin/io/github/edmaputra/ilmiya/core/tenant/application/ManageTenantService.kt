package io.github.edmaputra.ilmiya.core.tenant.application

import io.github.edmaputra.ilmiya.common.domain.TenantId
import io.github.edmaputra.ilmiya.core.tenant.domain.Tenant
import io.github.edmaputra.ilmiya.core.tenant.domain.exception.DuplicateTenantException
import io.github.edmaputra.ilmiya.core.tenant.domain.exception.TenantNotFoundException
import io.github.edmaputra.ilmiya.core.tenant.domain.port.`in`.CreateTenantCommand
import io.github.edmaputra.ilmiya.core.tenant.domain.port.`in`.CreateTenantUseCase
import io.github.edmaputra.ilmiya.core.tenant.domain.port.`in`.DeleteTenantUseCase
import io.github.edmaputra.ilmiya.core.tenant.domain.port.`in`.GetTenantUseCase
import io.github.edmaputra.ilmiya.core.tenant.domain.port.`in`.UpdateTenantCommand
import io.github.edmaputra.ilmiya.core.tenant.domain.port.`in`.UpdateTenantUseCase
import io.github.edmaputra.ilmiya.core.tenant.domain.port.out.TenantEventPublisher
import io.github.edmaputra.ilmiya.core.tenant.domain.port.out.TenantRepository

/**
 * Application service managing tenant foundation lifecycle operations.
 *
 * @property tenantRepository outbound persistence port for tenant aggregates
 * @property eventPublisher outbound publisher for tenant domain events
 * @author edmaputra
 * @since 0.0.1
 */
class ManageTenantService(
    private val tenantRepository: TenantRepository,
    private val eventPublisher: TenantEventPublisher
) : CreateTenantUseCase, GetTenantUseCase, UpdateTenantUseCase, DeleteTenantUseCase {

    override fun createTenant(command: CreateTenantCommand): Tenant {
        val tenantId = TenantId.of(command.tenantId.trim())
        if (tenantRepository.existsById(tenantId)) {
            throw DuplicateTenantException("A tenant with ID '${tenantId.value}' already exists.")
        }

        val tenant = Tenant.create(
            tenantId = tenantId,
            name = command.name.trim(),
            address = command.address.trim(),
            contactEmail = command.contactEmail.trim(),
            contactPhone = command.contactPhone.trim(),
            legalEntityNumber = command.legalEntityNumber?.trim(),
            registrationType = command.registrationType?.trim(),
            logoUrl = command.logoUrl?.trim()
        )
        val saved = tenantRepository.save(tenant)
        eventPublisher.publishAll(tenant.pullDomainEvents())
        return saved
    }

    override fun getTenant(tenantId: TenantId): Tenant =
        tenantRepository.findById(tenantId)
            ?: throw TenantNotFoundException("Tenant '${tenantId.value}' was not found.")

    override fun updateTenant(tenantId: TenantId, command: UpdateTenantCommand): Tenant {
        val existing = getTenant(tenantId)
        val updated = existing.updateProfile(
            name = command.name.trim(),
            address = command.address.trim(),
            contactEmail = command.contactEmail.trim(),
            contactPhone = command.contactPhone.trim(),
            logoUrl = command.logoUrl?.trim(),
            legalEntityNumber = command.legalEntityNumber?.trim(),
            registrationType = command.registrationType?.trim(),
            status = command.status
        )
        val saved = tenantRepository.save(updated)
        eventPublisher.publishAll(updated.pullDomainEvents())
        return saved
    }

    override fun deleteTenant(tenantId: TenantId) {
        if (!tenantRepository.existsById(tenantId)) {
            throw TenantNotFoundException("Tenant '${tenantId.value}' was not found.")
        }
        tenantRepository.deleteById(tenantId)
    }
}
