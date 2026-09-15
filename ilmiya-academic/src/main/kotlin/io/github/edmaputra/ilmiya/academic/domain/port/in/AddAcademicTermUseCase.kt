package io.github.edmaputra.ilmiya.academic.domain.port.`in`

import io.github.edmaputra.ilmiya.academic.domain.AcademicTerm
import io.github.edmaputra.ilmiya.academic.domain.AcademicYearId
import io.github.edmaputra.ilmiya.common.domain.TenantId

/**
 * Inbound port for adding an academic term to an academic year.
 *
 * @author edmaputra
 * @since 0.0.1
 */
interface AddAcademicTermUseCase {

    /**
     * Adds an academic term to the specified academic year.
     *
     * @param tenantId tenant boundary
     * @param academicYearId target academic year identifier
     * @param command input payload for the term
     * @return the newly added academic term
     * @throws io.github.edmaputra.ilmiya.academic.domain.exception.AcademicYearNotFoundException if academic year not found
     * @throws io.github.edmaputra.ilmiya.academic.domain.exception.InvalidAcademicYearDatesException if dates violate invariants
     */
    fun addTerm(tenantId: TenantId, academicYearId: AcademicYearId, command: AddAcademicTermCommand): AcademicTerm
}
