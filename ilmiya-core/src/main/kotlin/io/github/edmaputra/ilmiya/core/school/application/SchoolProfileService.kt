package io.github.edmaputra.ilmiya.core.school.application

import io.github.edmaputra.ilmiya.common.domain.TenantId
import io.github.edmaputra.ilmiya.core.school.domain.School
import io.github.edmaputra.ilmiya.core.school.domain.exception.SchoolNotFoundException
import io.github.edmaputra.ilmiya.core.school.domain.port.`in`.GetSchoolProfileUseCase
import io.github.edmaputra.ilmiya.core.school.domain.port.`in`.UpdateSchoolProfileCommand
import io.github.edmaputra.ilmiya.core.school.domain.port.`in`.UpdateSchoolProfileUseCase
import io.github.edmaputra.ilmiya.core.school.domain.port.out.SchoolEventPublisher
import io.github.edmaputra.ilmiya.core.school.domain.port.out.SchoolRepository

/**
 * Application service managing school profile retrieval and updates.
 *
 * @property schoolRepository persistence port for school entities
 * @property eventPublisher outbound publisher for school domain events
 * @author edmaputra
 * @since 0.0.1
 */
class SchoolProfileService(
    private val schoolRepository: SchoolRepository,
    private val eventPublisher: SchoolEventPublisher
) : GetSchoolProfileUseCase, UpdateSchoolProfileUseCase {

    override fun getProfile(tenantId: TenantId): School =
        schoolRepository.findByTenantId(tenantId)
            ?: throw SchoolNotFoundException("School profile for tenant '${tenantId.value}' was not found.")

    override fun updateProfile(tenantId: TenantId, command: UpdateSchoolProfileCommand): School {
        val existing = getProfile(tenantId)
        val updated = existing.updateProfile(
            name = command.name,
            address = command.address,
            contactEmail = command.contactEmail,
            contactPhone = command.contactPhone,
            logoUrl = command.logoUrl,
            governmentRegistrationNumber = command.governmentRegistrationNumber,
            registrationType = command.registrationType,
            accreditation = command.accreditation
        )
        val saved = schoolRepository.save(updated)
        eventPublisher.publishAll(updated.pullDomainEvents())
        return saved
    }
}
