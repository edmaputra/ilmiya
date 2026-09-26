package io.github.edmaputra.ilmiya.core.school.application

import io.github.edmaputra.ilmiya.common.domain.TenantId
import io.github.edmaputra.ilmiya.core.school.domain.School
import io.github.edmaputra.ilmiya.core.school.domain.exception.DuplicateInstitutionCodeException
import io.github.edmaputra.ilmiya.core.school.domain.exception.DuplicateTenantException
import io.github.edmaputra.ilmiya.core.school.domain.exception.TenantNotFoundException
import io.github.edmaputra.ilmiya.core.school.domain.port.`in`.CreateTenantCommand
import io.github.edmaputra.ilmiya.core.school.domain.port.`in`.CreateTenantUseCase
import io.github.edmaputra.ilmiya.core.school.domain.port.`in`.DeleteTenantUseCase
import io.github.edmaputra.ilmiya.core.school.domain.port.`in`.GetTenantUseCase
import io.github.edmaputra.ilmiya.core.school.domain.port.`in`.UpdateTenantCommand
import io.github.edmaputra.ilmiya.core.school.domain.port.`in`.UpdateTenantUseCase
import io.github.edmaputra.ilmiya.core.school.domain.port.out.SchoolEventPublisher
import io.github.edmaputra.ilmiya.core.school.domain.port.out.SchoolRepository

/**
 * Application service managing tenant CRUD lifecycle operations.
 *
 * @property schoolRepository outbound persistence port for school aggregates
 * @property eventPublisher outbound publisher for domain events
 * @author edmaputra
 * @since 0.0.1
 */
class ManageTenantService(
    private val schoolRepository: SchoolRepository,
    private val eventPublisher: SchoolEventPublisher
) : CreateTenantUseCase, GetTenantUseCase, UpdateTenantUseCase, DeleteTenantUseCase {

    override fun createTenant(command: CreateTenantCommand): School {
        val normalizedCode = command.institutionCode.trim().uppercase()
        if (schoolRepository.existsByInstitutionCode(normalizedCode)) {
            throw DuplicateInstitutionCodeException("A tenant with institution code '$normalizedCode' already exists.")
        }
        val tenantId = TenantId.of(command.tenantId)
        if (schoolRepository.existsByTenantId(tenantId)) {
            throw DuplicateTenantException("A tenant with ID '${tenantId.value}' already exists.")
        }

        val school = School.create(
            tenantId = tenantId,
            name = command.name,
            institutionCode = normalizedCode,
            governmentRegistrationNumber = command.governmentRegistrationNumber,
            registrationType = command.registrationType,
            address = command.address,
            contactEmail = command.contactEmail,
            contactPhone = command.contactPhone,
            logoUrl = command.logoUrl,
            accreditation = command.accreditation,
            operationalSettings = command.operationalSettings
        )
        val saved = schoolRepository.save(school)
        eventPublisher.publishAll(school.pullDomainEvents())
        return saved
    }

    override fun getTenant(tenantId: TenantId): School =
        schoolRepository.findByTenantId(tenantId)
            ?: throw TenantNotFoundException("Tenant '${tenantId.value}' was not found.")

    override fun updateTenant(tenantId: TenantId, command: UpdateTenantCommand): School {
        val existing = getTenant(tenantId)
        val updated = existing.updateProfile(
            name = command.name,
            address = command.address,
            contactEmail = command.contactEmail,
            contactPhone = command.contactPhone,
            logoUrl = command.logoUrl,
            governmentRegistrationNumber = command.governmentRegistrationNumber,
            registrationType = command.registrationType,
            accreditation = command.accreditation,
            status = command.status
        )
        val saved = schoolRepository.save(updated)
        eventPublisher.publishAll(updated.pullDomainEvents())
        return saved
    }

    override fun deleteTenant(tenantId: TenantId) {
        if (!schoolRepository.existsByTenantId(tenantId)) {
            throw TenantNotFoundException("Tenant '${tenantId.value}' was not found.")
        }
        schoolRepository.deleteByTenantId(tenantId)
    }
}
