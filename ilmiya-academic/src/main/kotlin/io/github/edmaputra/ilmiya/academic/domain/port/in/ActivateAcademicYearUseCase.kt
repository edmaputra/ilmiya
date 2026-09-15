package io.github.edmaputra.ilmiya.academic.domain.port.`in`

import io.github.edmaputra.ilmiya.academic.domain.AcademicYear
import io.github.edmaputra.ilmiya.academic.domain.AcademicYearId
import io.github.edmaputra.ilmiya.common.domain.TenantId

/**
 * Inbound port for activating an academic year and archiving previous active years.
 *
 * @author edmaputra
 * @since 0.0.1
 */
interface ActivateAcademicYearUseCase {

    /**
     * Activates the target academic year, ensuring only one active academic year exists per tenant.
     *
     * @param tenantId tenant identifier
     * @param academicYearId target academic year to activate
     * @return the activated academic year aggregate
     * @throws io.github.edmaputra.ilmiya.academic.domain.exception.AcademicYearNotFoundException if not found
     */
    fun activate(tenantId: TenantId, academicYearId: AcademicYearId): AcademicYear
}
