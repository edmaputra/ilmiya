package io.github.edmaputra.ilmiya.academic.domain.port.`in`

import io.github.edmaputra.ilmiya.academic.domain.AcademicYear
import io.github.edmaputra.ilmiya.academic.domain.AcademicYearId
import io.github.edmaputra.ilmiya.common.domain.TenantId

/**
 * Inbound port for querying academic years for a tenant.
 *
 * @author edmaputra
 */
interface GetAcademicYearsUseCase {

    /**
     * Lists all academic years belonging to the specified tenant.
     *
     * @param tenantId tenant identifier
     * @return list of academic year aggregates
     */
    fun listByTenant(tenantId: TenantId): List<AcademicYear>

    /**
     * Gets the currently active academic year for the tenant, if any.
     *
     * @param tenantId tenant identifier
     * @return the active academic year, or null
     */
    fun getActiveAcademicYear(tenantId: TenantId): AcademicYear?

    /**
     * Retrieves an academic year by identifier within tenant boundary.
     *
     * @param tenantId tenant identifier
     * @param id academic year identifier
     * @return the academic year aggregate
     * @throws io.github.edmaputra.ilmiya.academic.domain.exception.AcademicYearNotFoundException if not found
     */
    fun getById(tenantId: TenantId, id: AcademicYearId): AcademicYear
}
