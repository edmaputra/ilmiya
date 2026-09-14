package io.github.edmaputra.ilmiya.core.school.adapter.persistence

import io.github.edmaputra.ilmiya.common.domain.TenantId
import io.github.edmaputra.ilmiya.core.school.domain.Accreditation
import io.github.edmaputra.ilmiya.core.school.domain.GradingPreference
import io.github.edmaputra.ilmiya.core.school.domain.OperationalSettings
import io.github.edmaputra.ilmiya.core.school.domain.School
import io.github.edmaputra.ilmiya.core.school.domain.SchoolId
import io.github.edmaputra.ilmiya.core.school.domain.SchoolStatus
import io.github.edmaputra.ilmiya.core.school.domain.TermLayout
import io.github.edmaputra.ilmiya.core.school.domain.port.out.SchoolRepository
import org.springframework.stereotype.Repository
import java.time.DayOfWeek

/**
 * Outbound persistence adapter implementing [SchoolRepository] using Spring Data JPA.
 *
 * @author edmaputra
 */
@Repository
class SchoolRepositoryAdapter(
    private val springDataRepository: SpringDataSchoolRepository
) : SchoolRepository {

    override fun save(school: School): School {
        val entity = toEntity(school)
        val saved = springDataRepository.save(entity)
        return toDomain(saved)
    }

    override fun findById(id: SchoolId): School? =
        springDataRepository.findById(id.value).map { toDomain(it) }.orElse(null)

    override fun findByTenantId(tenantId: TenantId): School? =
        springDataRepository.findByTenantId(tenantId.value).map { toDomain(it) }.orElse(null)

    override fun findByInstitutionCode(institutionCode: String): School? =
        springDataRepository.findByInstitutionCode(institutionCode).map { toDomain(it) }.orElse(null)

    override fun existsByInstitutionCode(institutionCode: String): Boolean =
        springDataRepository.existsByInstitutionCode(institutionCode)

    override fun existsByTenantId(tenantId: TenantId): Boolean =
        springDataRepository.existsByTenantId(tenantId.value)

    private fun toEntity(domain: School): SchoolJpaEntity = SchoolJpaEntity(
        id = domain.id.value,
        tenantId = domain.tenantId.value,
        name = domain.name,
        institutionCode = domain.institutionCode,
        governmentRegistrationNumber = domain.governmentRegistrationNumber,
        registrationType = domain.registrationType,
        address = domain.address,
        contactEmail = domain.contactEmail,
        contactPhone = domain.contactPhone,
        logoUrl = domain.logoUrl,
        accreditationStatus = domain.accreditation.status,
        accreditationBody = domain.accreditation.accreditingBody,
        accreditationValidUntil = domain.accreditation.validUntil,
        workingDays = domain.operationalSettings.workingDays.joinToString(",") { it.name },
        gradingPreference = domain.operationalSettings.gradingPreference.name,
        termLayout = domain.operationalSettings.termLayout.name,
        status = domain.status.name,
        createdAt = domain.createdAt,
        updatedAt = domain.updatedAt
    )

    private fun toDomain(entity: SchoolJpaEntity): School {
        val workingDays = entity.workingDays.split(",")
            .filter { it.isNotBlank() }
            .map { DayOfWeek.valueOf(it.trim()) }
            .toSet()

        return School(
            id = SchoolId.of(entity.id),
            tenantId = TenantId.of(entity.tenantId),
            name = entity.name,
            institutionCode = entity.institutionCode,
            governmentRegistrationNumber = entity.governmentRegistrationNumber,
            registrationType = entity.registrationType,
            address = entity.address,
            contactEmail = entity.contactEmail,
            contactPhone = entity.contactPhone,
            logoUrl = entity.logoUrl,
            accreditation = Accreditation(
                status = entity.accreditationStatus,
                accreditingBody = entity.accreditationBody,
                validUntil = entity.accreditationValidUntil
            ),
            operationalSettings = OperationalSettings(
                workingDays = if (workingDays.isNotEmpty()) workingDays else setOf(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY),
                gradingPreference = GradingPreference.valueOf(entity.gradingPreference),
                termLayout = TermLayout.valueOf(entity.termLayout)
            ),
            status = SchoolStatus.valueOf(entity.status),
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }
}
