package io.github.edmaputra.ilmiya.core.school.application

import io.github.edmaputra.ilmiya.common.domain.TenantId
import io.github.edmaputra.ilmiya.core.school.domain.School
import io.github.edmaputra.ilmiya.core.school.domain.exception.DuplicateInstitutionCodeException
import io.github.edmaputra.ilmiya.core.school.domain.port.`in`.RegisterSchoolCommand
import io.github.edmaputra.ilmiya.core.school.domain.port.`in`.RegisterSchoolUseCase
import io.github.edmaputra.ilmiya.core.school.domain.port.out.SchoolEventPublisher
import io.github.edmaputra.ilmiya.core.school.domain.port.out.SchoolRepository

/**
 * Application service orchestrating the registration and provisioning of school institutions.
 *
 * @property schoolRepository persistence port for school aggregates
 * @property eventPublisher outbound publisher for school domain events
 * @author edmaputra
 */
class RegisterSchoolService(
    private val schoolRepository: SchoolRepository,
    private val eventPublisher: SchoolEventPublisher
) : RegisterSchoolUseCase {

    override fun register(command: RegisterSchoolCommand): School {
        val normalizedCode = command.institutionCode.trim().uppercase()
        if (schoolRepository.existsByInstitutionCode(normalizedCode)) {
            throw DuplicateInstitutionCodeException("A school with institution code '$normalizedCode' already exists.")
        }

        val tenantId = TenantId.of(command.tenantId)
        if (schoolRepository.existsByTenantId(tenantId)) {
            throw DuplicateInstitutionCodeException("A school with tenant ID '${tenantId.value}' already exists.")
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

        val savedSchool = schoolRepository.save(school)
        eventPublisher.publishAll(school.pullDomainEvents())
        return savedSchool
    }
}
