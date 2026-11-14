package io.github.edmaputra.ilmiya.core.tenant.adapter.persistence

import io.github.edmaputra.ilmiya.common.domain.TenantId
import io.github.edmaputra.ilmiya.core.tenant.domain.Tenant
import io.github.edmaputra.ilmiya.core.tenant.domain.TenantStatus
import io.github.edmaputra.ilmiya.core.tenant.domain.port.out.TenantRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Repository

/**
 * Outbound persistence adapter implementing [TenantRepository] using Spring Data JPA.
 *
 * @author edmaputra
 * @since 0.0.1
 */
@Repository
class TenantRepositoryAdapter(
    private val springDataRepository: SpringDataTenantRepository
) : TenantRepository {

    override fun save(tenant: Tenant): Tenant {
        val existingOpt = springDataRepository.findById(tenant.id.value)
        val entity = if (existingOpt.isPresent) {
            val existing = existingOpt.get()
            existing.name = tenant.name
            existing.legalEntityNumber = tenant.legalEntityNumber
            existing.registrationType = tenant.registrationType
            existing.address = tenant.address
            existing.contactEmail = tenant.contactEmail
            existing.contactPhone = tenant.contactPhone
            existing.logoUrl = tenant.logoUrl
            existing.status = tenant.status.name
            existing.updatedAt = tenant.updatedAt
            existing
        } else {
            toEntity(tenant)
        }
        val saved = springDataRepository.save(entity)
        return toDomain(saved)
    }

    override fun findById(id: TenantId): Tenant? =
        springDataRepository.findById(id.value).map { toDomain(it) }.orElse(null)

    override fun existsById(id: TenantId): Boolean =
        springDataRepository.existsById(id.value)

    override fun findAll(page: Int, size: Int): List<Tenant> =
        springDataRepository.findAll(
            PageRequest.of(page, size, Sort.by("name").ascending())
        ).content.map { toDomain(it) }

    override fun count(): Long = springDataRepository.count()

    override fun deleteById(id: TenantId) {
        springDataRepository.deleteById(id.value)
    }

    private fun toEntity(domain: Tenant): TenantJpaEntity = TenantJpaEntity(
        id = domain.id.value,
        name = domain.name,
        legalEntityNumber = domain.legalEntityNumber,
        registrationType = domain.registrationType,
        address = domain.address,
        contactEmail = domain.contactEmail,
        contactPhone = domain.contactPhone,
        logoUrl = domain.logoUrl,
        status = domain.status.name,
        createdAt = domain.createdAt,
        updatedAt = domain.updatedAt
    )

    private fun toDomain(entity: TenantJpaEntity): Tenant = Tenant(
        id = TenantId.of(entity.id),
        name = entity.name,
        legalEntityNumber = entity.legalEntityNumber,
        registrationType = entity.registrationType,
        address = entity.address,
        contactEmail = entity.contactEmail,
        contactPhone = entity.contactPhone,
        logoUrl = entity.logoUrl,
        status = TenantStatus.valueOf(entity.status),
        createdAt = entity.createdAt,
        updatedAt = entity.updatedAt
    )
}
