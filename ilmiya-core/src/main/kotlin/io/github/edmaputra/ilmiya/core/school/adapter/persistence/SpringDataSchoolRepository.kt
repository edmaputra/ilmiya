package io.github.edmaputra.ilmiya.core.school.adapter.persistence

import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

/**
 * Spring Data JPA repository for school persistence entities.
 *
 * @author edmaputra
 * @since 0.0.1
 */
interface SpringDataSchoolRepository : JpaRepository<SchoolJpaEntity, String> {

    fun findByTenantId(tenantId: String): Optional<SchoolJpaEntity>

    fun findByInstitutionCode(institutionCode: String): Optional<SchoolJpaEntity>

    fun existsByInstitutionCode(institutionCode: String): Boolean

    fun existsByTenantId(tenantId: String): Boolean
}
