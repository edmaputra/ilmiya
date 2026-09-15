package io.github.edmaputra.ilmiya.academic.adapter.persistence

import io.github.edmaputra.ilmiya.academic.domain.AcademicTerm
import io.github.edmaputra.ilmiya.academic.domain.AcademicTermId
import io.github.edmaputra.ilmiya.academic.domain.AcademicTermStatus
import io.github.edmaputra.ilmiya.academic.domain.AcademicTermType
import io.github.edmaputra.ilmiya.academic.domain.AcademicYear
import io.github.edmaputra.ilmiya.academic.domain.AcademicYearId
import io.github.edmaputra.ilmiya.academic.domain.AcademicYearStatus
import io.github.edmaputra.ilmiya.academic.domain.port.out.AcademicYearRepository
import io.github.edmaputra.ilmiya.common.domain.TenantId
import org.springframework.stereotype.Repository

/**
 * Outbound persistence adapter implementing [AcademicYearRepository] using Spring Data JPA.
 *
 * @author edmaputra
 * @since 0.0.1
 */
@Repository
class AcademicYearRepositoryAdapter(
    private val springDataRepository: SpringDataAcademicYearRepository
) : AcademicYearRepository {

    override fun save(academicYear: AcademicYear): AcademicYear {
        val entity = toEntity(academicYear)
        val saved = springDataRepository.save(entity)
        return toDomain(saved)
    }

    override fun findById(tenantId: TenantId, id: AcademicYearId): AcademicYear? =
        springDataRepository.findByTenantIdAndId(tenantId.value, id.value)
            .map { toDomain(it) }
            .orElse(null)

    override fun findByName(tenantId: TenantId, name: String): AcademicYear? =
        springDataRepository.findByTenantIdAndName(tenantId.value, name)
            .map { toDomain(it) }
            .orElse(null)

    override fun findActive(tenantId: TenantId): AcademicYear? =
        springDataRepository.findByTenantIdAndStatus(tenantId.value, AcademicYearStatus.ACTIVE.name)
            .map { toDomain(it) }
            .orElse(null)

    override fun findAllByTenantId(tenantId: TenantId): List<AcademicYear> =
        springDataRepository.findAllByTenantIdOrderByStartDateDesc(tenantId.value)
            .map { toDomain(it) }

    override fun existsByName(tenantId: TenantId, name: String): Boolean =
        springDataRepository.existsByTenantIdAndName(tenantId.value, name)

    private fun toEntity(domain: AcademicYear): AcademicYearJpaEntity {
        val entity = AcademicYearJpaEntity(
            id = domain.id.value,
            tenantId = domain.tenantId.value,
            name = domain.name,
            startDate = domain.startDate,
            endDate = domain.endDate,
            status = domain.status.name,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt
        )
        entity.terms = domain.terms.map { term ->
            AcademicTermJpaEntity(
                id = term.id.value,
                academicYearId = domain.id.value,
                name = term.name,
                termType = term.type.name,
                displayName = term.displayName,
                startDate = term.startDate,
                endDate = term.endDate,
                status = term.status.name
            )
        }.toMutableList()
        return entity
    }

    private fun toDomain(entity: AcademicYearJpaEntity): AcademicYear = AcademicYear(
        id = AcademicYearId.of(entity.id),
        tenantId = TenantId.of(entity.tenantId),
        name = entity.name,
        startDate = entity.startDate,
        endDate = entity.endDate,
        status = AcademicYearStatus.valueOf(entity.status),
        terms = entity.terms.map { termEntity ->
            AcademicTerm(
                id = AcademicTermId.of(termEntity.id),
                name = termEntity.name,
                type = AcademicTermType.valueOf(termEntity.termType),
                displayName = termEntity.displayName,
                startDate = termEntity.startDate,
                endDate = termEntity.endDate,
                status = AcademicTermStatus.valueOf(termEntity.status)
            )
        },
        createdAt = entity.createdAt,
        updatedAt = entity.updatedAt
    )
}
