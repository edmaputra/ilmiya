package io.github.edmaputra.ilmiya.core.tenant.adapter.persistence

import org.springframework.data.jpa.repository.JpaRepository

/**
 * Spring Data JPA repository for [TenantJpaEntity].
 *
 * @author edmaputra
 * @since 0.0.1
 */
interface SpringDataTenantRepository : JpaRepository<TenantJpaEntity, String>
