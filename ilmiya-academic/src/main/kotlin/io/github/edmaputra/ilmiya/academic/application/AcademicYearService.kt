package io.github.edmaputra.ilmiya.academic.application

import io.github.edmaputra.ilmiya.academic.domain.AcademicTerm
import io.github.edmaputra.ilmiya.academic.domain.AcademicTermId
import io.github.edmaputra.ilmiya.academic.domain.AcademicYear
import io.github.edmaputra.ilmiya.academic.domain.AcademicYearId
import io.github.edmaputra.ilmiya.academic.domain.exception.AcademicYearNotFoundException
import io.github.edmaputra.ilmiya.academic.domain.exception.DuplicateAcademicYearException
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

/**
 * Application service orchestrating academic year creation, activation, terms, and queries.
 * Pure Kotlin service adhering to hexagonal architecture (zero Spring annotations).
 *
 * @property academicYearRepository persistence port for academic years
 * @property eventPublisher outbound domain event publisher
 * @author edmaputra
 */
class AcademicYearService(
    private val academicYearRepository: AcademicYearRepository,
    private val eventPublisher: AcademicYearEventPublisher
) : CreateAcademicYearUseCase,
    ActivateAcademicYearUseCase,
    GetAcademicYearsUseCase,
    AddAcademicTermUseCase,
    ActivateAcademicTermUseCase {

    override fun create(tenantId: TenantId, command: CreateAcademicYearCommand): AcademicYear {
        if (academicYearRepository.existsByName(tenantId, command.name.trim())) {
            throw DuplicateAcademicYearException(
                "Academic year with name '${command.name}' already exists for this institution."
            )
        }

        val academicYear = AcademicYear.create(
            tenantId = tenantId,
            name = command.name,
            startDate = command.startDate,
            endDate = command.endDate
        )

        val saved = academicYearRepository.save(academicYear)
        eventPublisher.publishAll(academicYear.pullDomainEvents())
        return saved
    }

    override fun activate(tenantId: TenantId, academicYearId: AcademicYearId): AcademicYear {
        val targetYear = getById(tenantId, academicYearId)

        // Ensure only one academic year is active per tenant at any time
        val currentActive = academicYearRepository.findActive(tenantId)
        if (currentActive != null && currentActive.id != academicYearId) {
            val archived = currentActive.archive()
            academicYearRepository.save(archived)
        }

        val activated = targetYear.activate()
        val saved = academicYearRepository.save(activated)
        eventPublisher.publishAll(activated.pullDomainEvents())
        return saved
    }

    override fun listByTenant(tenantId: TenantId): List<AcademicYear> =
        academicYearRepository.findAllByTenantId(tenantId)

    override fun getActiveAcademicYear(tenantId: TenantId): AcademicYear? =
        academicYearRepository.findActive(tenantId)

    override fun getById(tenantId: TenantId, id: AcademicYearId): AcademicYear =
        academicYearRepository.findById(tenantId, id)
            ?: throw AcademicYearNotFoundException("Academic year with ID '${id.value}' was not found.")

    override fun addTerm(
        tenantId: TenantId,
        academicYearId: AcademicYearId,
        command: AddAcademicTermCommand
    ): AcademicTerm {
        val year = getById(tenantId, academicYearId)
        val term = AcademicTerm(
            id = AcademicTermId.generate(),
            name = command.name.trim(),
            type = command.type,
            displayName = command.displayName.trim(),
            startDate = command.startDate,
            endDate = command.endDate
        )

        val updatedYear = year.addTerm(term)
        academicYearRepository.save(updatedYear)
        eventPublisher.publishAll(updatedYear.pullDomainEvents())
        return term
    }

    override fun activateTerm(
        tenantId: TenantId,
        academicYearId: AcademicYearId,
        termId: AcademicTermId
    ): AcademicTerm {
        val year = getById(tenantId, academicYearId)
        val updatedYear = year.activateTerm(termId)
        academicYearRepository.save(updatedYear)
        return updatedYear.terms.first { it.id == termId }
    }
}
