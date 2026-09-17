package io.github.edmaputra.ilmiya.academic.domain.port.out

import io.github.edmaputra.ilmiya.academic.domain.AcademicYear
import io.github.edmaputra.ilmiya.academic.domain.AcademicYearId
import io.github.edmaputra.ilmiya.common.domain.TenantId

/**
 * Outbound persistence port for persisting and querying [AcademicYear] aggregates.
 *
 * @author edmaputra
 * @since 0.0.1
 */
interface AcademicYearRepository {

    /**
     * Saves an academic year entity.
     *
     * @param academicYear domain entity to persist
     * @return persisted domain entity
     */
    fun save(academicYear: AcademicYear): AcademicYear

    /**
     * Finds an academic year by its identifier within a tenant.
     *
     * @param tenantId tenant identifier
     * @param id academic year identifier
     * @return academic year if found, null otherwise
     */
    fun findById(tenantId: TenantId, id: AcademicYearId): AcademicYear?

    /**
     * Finds an academic year by name within a tenant.
     *
     * @param tenantId tenant identifier
     * @param name academic year name
     * @return academic year if found, null otherwise
     */
    fun findByName(tenantId: TenantId, name: String): AcademicYear?

    /**
     * Finds the currently active academic year for a tenant.
     *
     * @param tenantId tenant identifier
     * @return active academic year if found, null otherwise
     */
    fun findActive(tenantId: TenantId): AcademicYear?

    /**
     * Retrieves all academic years for a tenant ordered by start date descending.
     *
     * @param tenantId tenant identifier
     * @return list of academic years
     */
    fun findAllByTenantId(tenantId: TenantId): List<AcademicYear>

    /**
     * Checks if an academic year exists with the given name for a tenant.
     *
     * @param tenantId tenant identifier
     * @param name academic year name
     * @return true if exists, false otherwise
     */
    fun existsByName(tenantId: TenantId, name: String): Boolean
}
