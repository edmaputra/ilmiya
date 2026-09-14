package io.github.edmaputra.ilmiya.app.academic

import io.github.edmaputra.ilmiya.academic.application.AcademicYearService
import io.github.edmaputra.ilmiya.academic.domain.AcademicTerm
import io.github.edmaputra.ilmiya.academic.domain.AcademicTermId
import io.github.edmaputra.ilmiya.academic.domain.AcademicYear
import io.github.edmaputra.ilmiya.academic.domain.AcademicYearId
import io.github.edmaputra.ilmiya.academic.domain.port.`in`.ActivateAcademicTermUseCase
import io.github.edmaputra.ilmiya.academic.domain.port.`in`.ActivateAcademicYearUseCase
import io.github.edmaputra.ilmiya.academic.domain.port.`in`.AddAcademicTermCommand
import io.github.edmaputra.ilmiya.academic.domain.port.`in`.AddAcademicTermUseCase
import io.github.edmaputra.ilmiya.academic.domain.port.`in`.CreateAcademicYearCommand
import io.github.edmaputra.ilmiya.academic.domain.port.`in`.CreateAcademicYearUseCase
import io.github.edmaputra.ilmiya.academic.domain.port.`in`.GetAcademicYearsUseCase
import io.github.edmaputra.ilmiya.academic.domain.port.out.AcademicYearEventPublisher
import io.github.edmaputra.ilmiya.academic.domain.port.out.AcademicYearRepository
import io.github.edmaputra.ilmiya.common.domain.TenantId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Transactional decorator for academic year and term use cases.
 *
 * @author edmaputra
 */
@Service
class TransactionalAcademicYearUseCase(
    academicYearRepository: AcademicYearRepository,
    eventPublisher: AcademicYearEventPublisher
) : CreateAcademicYearUseCase,
    ActivateAcademicYearUseCase,
    GetAcademicYearsUseCase,
    AddAcademicTermUseCase,
    ActivateAcademicTermUseCase {

    private val delegate = AcademicYearService(academicYearRepository, eventPublisher)

    @Transactional
    override fun create(tenantId: TenantId, command: CreateAcademicYearCommand): AcademicYear =
        delegate.create(tenantId, command)

    @Transactional
    override fun activate(tenantId: TenantId, academicYearId: AcademicYearId): AcademicYear =
        delegate.activate(tenantId, academicYearId)

    @Transactional(readOnly = true)
    override fun listByTenant(tenantId: TenantId): List<AcademicYear> =
        delegate.listByTenant(tenantId)

    @Transactional(readOnly = true)
    override fun getActiveAcademicYear(tenantId: TenantId): AcademicYear? =
        delegate.getActiveAcademicYear(tenantId)

    @Transactional(readOnly = true)
    override fun getById(tenantId: TenantId, id: AcademicYearId): AcademicYear =
        delegate.getById(tenantId, id)

    @Transactional
    override fun addTerm(
        tenantId: TenantId,
        academicYearId: AcademicYearId,
        command: AddAcademicTermCommand
    ): AcademicTerm = delegate.addTerm(tenantId, academicYearId, command)

    @Transactional
    override fun activateTerm(
        tenantId: TenantId,
        academicYearId: AcademicYearId,
        termId: AcademicTermId
    ): AcademicTerm = delegate.activateTerm(tenantId, academicYearId, termId)
}
