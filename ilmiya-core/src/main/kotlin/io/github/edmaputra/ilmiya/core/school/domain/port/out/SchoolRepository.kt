package io.github.edmaputra.ilmiya.core.school.domain.port.out

import io.github.edmaputra.ilmiya.common.domain.TenantId
import io.github.edmaputra.ilmiya.core.school.domain.School
import io.github.edmaputra.ilmiya.core.school.domain.SchoolId

/**
 * Outbound repository port for persisting and querying [School] aggregates.
 *
 * @author edmaputra
 * @since 0.0.1
 */
interface SchoolRepository {

    /**
     * Saves a school entity.
     *
     * @param school domain entity to persist
     * @return persisted domain entity
     */
    fun save(school: School): School

    /**
     * Finds a school by its identifier.
     *
     * @param id school identifier
     * @return school if found, null otherwise
     */
    fun findById(id: SchoolId): School?

    /**
     * Finds a school by tenant identifier.
     *
     * @param tenantId tenant identifier
     * @return school if found, null otherwise
     */
    fun findByTenantId(tenantId: TenantId): School?

    /**
     * Finds a school by institution code.
     *
     * @param institutionCode unique institution code
     * @return school if found, null otherwise
     */
    fun findByInstitutionCode(institutionCode: String): School?

    /**
     * Checks if a school exists with the given institution code.
     *
     * @param institutionCode institution code to check
     * @return true if exists, false otherwise
     */
    fun existsByInstitutionCode(institutionCode: String): Boolean

    /**
     * Checks if a school exists for the given tenant identifier.
     *
     * @param tenantId tenant identifier to check
     * @return true if exists, false otherwise
     */
    fun existsByTenantId(tenantId: TenantId): Boolean

    /**
     * Retrieves a paginated list of schools.
     *
     * @param page zero-based page index
     * @param size page size
     * @return list of schools for the specified page
     */
    fun findAll(page: Int, size: Int): List<School>

    /**
     * Counts the total number of registered schools.
     *
     * @return total count of schools
     */
    fun count(): Long

    /**
     * Deletes a school by its tenant identifier.
     *
     * @param tenantId tenant identifier
     */
    fun deleteByTenantId(tenantId: TenantId)
}
