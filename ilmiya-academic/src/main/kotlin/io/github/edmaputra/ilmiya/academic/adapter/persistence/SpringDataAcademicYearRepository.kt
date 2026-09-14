package io.github.edmaputra.ilmiya.academic.adapter.persistence

import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

/**
 * Spring Data JPA repository for academic years.
 *
 * @author edmaputra
 */
interface SpringDataAcademicYearRepository : JpaRepository<AcademicYearJpaEntity, String> {

    fun findByTenantIdAndId(tenantId: String, id: String): Optional<AcademicYearJpaEntity>

    fun findByTenantIdAndName(tenantId: String, name: String): Optional<AcademicYearJpaEntity>

    fun findByTenantIdAndStatus(tenantId: String, status: String): Optional<AcademicYearJpaEntity>

    fun findAllByTenantIdOrderByStartDateDesc(tenantId: String): List<AcademicYearJpaEntity>

    fun existsByTenantIdAndName(tenantId: String, name: String): Boolean
}
