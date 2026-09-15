package io.github.edmaputra.ilmiya.academic.domain.port.`in`

import io.github.edmaputra.ilmiya.academic.domain.AcademicYear
import io.github.edmaputra.ilmiya.common.domain.TenantId

/**
 * Inbound port for creating and registering an institutional academic year.
 *
 * @author edmaputra
 * @since 0.0.1
 */
interface CreateAcademicYearUseCase {

    /**
     * Creates a new academic year under the specified tenant.
     *
     * @param tenantId the tenant boundary
     * @param command input payload for the academic year
     * @return the created academic year aggregate
     * @throws io.github.edmaputra.ilmiya.academic.domain.exception.DuplicateAcademicYearException if name already exists
     */
    fun create(tenantId: TenantId, command: CreateAcademicYearCommand): AcademicYear
}
