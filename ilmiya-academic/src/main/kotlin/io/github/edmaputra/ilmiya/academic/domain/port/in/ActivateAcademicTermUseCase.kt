package io.github.edmaputra.ilmiya.academic.domain.port.`in`

import io.github.edmaputra.ilmiya.academic.domain.AcademicTerm
import io.github.edmaputra.ilmiya.academic.domain.AcademicTermId
import io.github.edmaputra.ilmiya.academic.domain.AcademicYearId
import io.github.edmaputra.ilmiya.common.domain.TenantId

/**
 * Inbound port for activating an academic term within an academic year.
 *
 * @author edmaputra
 * @since 0.0.1
 */
interface ActivateAcademicTermUseCase {

    /**
     * Activates the specified academic term and archives other active terms in the same academic year.
     *
     * @param tenantId tenant boundary
     * @param academicYearId parent academic year identifier
     * @param termId target term identifier to activate
     * @return the activated academic term
     * @throws io.github.edmaputra.ilmiya.academic.domain.exception.AcademicYearNotFoundException if year not found
     * @throws io.github.edmaputra.ilmiya.academic.domain.exception.AcademicTermNotFoundException if term not found
     */
    fun activateTerm(tenantId: TenantId, academicYearId: AcademicYearId, termId: AcademicTermId): AcademicTerm
}
