package io.github.edmaputra.ilmiya.academic.adapter.web

import io.github.edmaputra.ilmiya.academic.domain.AcademicTermId
import io.github.edmaputra.ilmiya.academic.domain.AcademicYearId
import io.github.edmaputra.ilmiya.academic.domain.port.`in`.ActivateAcademicTermUseCase
import io.github.edmaputra.ilmiya.academic.domain.port.`in`.ActivateAcademicYearUseCase
import io.github.edmaputra.ilmiya.academic.domain.port.`in`.AddAcademicTermUseCase
import io.github.edmaputra.ilmiya.academic.domain.port.`in`.CreateAcademicYearUseCase
import io.github.edmaputra.ilmiya.academic.domain.port.`in`.GetAcademicYearsUseCase
import io.github.edmaputra.ilmiya.common.context.TenantContext
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI

/**
 * REST controller exposing endpoints for academic year and term management.
 *
 * @author edmaputra
 * @since 0.0.1
 */
@RestController
@RequestMapping("/api/v1/academic-years")
class AcademicYearController(
    private val createAcademicYearUseCase: CreateAcademicYearUseCase,
    private val activateAcademicYearUseCase: ActivateAcademicYearUseCase,
    private val getAcademicYearsUseCase: GetAcademicYearsUseCase,
    private val addAcademicTermUseCase: AddAcademicTermUseCase,
    private val activateAcademicTermUseCase: ActivateAcademicTermUseCase
) {

    /**
     * Creates a new academic year under current tenant context.
     */
    @PostMapping
    fun createAcademicYear(
        @Valid @RequestBody request: CreateAcademicYearRequest
    ): ResponseEntity<AcademicYearResponse> {
        val tenantId = TenantContext.requireTenantId()
        val created = createAcademicYearUseCase.create(tenantId, request.toCommand())
        val response = AcademicYearResponse.from(created)
        val location = URI.create("/api/v1/academic-years/${created.id.value}")
        return ResponseEntity.created(location).body(response)
    }

    /**
     * Lists all academic years for the current tenant.
     */
    @GetMapping
    fun listAcademicYears(): ResponseEntity<List<AcademicYearResponse>> {
        val tenantId = TenantContext.requireTenantId()
        val years = getAcademicYearsUseCase.listByTenant(tenantId).map { AcademicYearResponse.from(it) }
        return ResponseEntity.ok(years)
    }

    /**
     * Retrieves the currently active academic year for the tenant.
     */
    @GetMapping("/active")
    fun getActiveAcademicYear(): ResponseEntity<AcademicYearResponse> {
        val tenantId = TenantContext.requireTenantId()
        val activeYear = getAcademicYearsUseCase.getActiveAcademicYear(tenantId)
        return activeYear?.let { ResponseEntity.ok(AcademicYearResponse.from(it)) }
            ?: ResponseEntity.notFound().build()
    }

    /**
     * Retrieves a single academic year by its ID.
     */
    @GetMapping("/{id}")
    fun getById(@PathVariable id: String): ResponseEntity<AcademicYearResponse> {
        val tenantId = TenantContext.requireTenantId()
        val year = getAcademicYearsUseCase.getById(tenantId, AcademicYearId.of(id))
        return ResponseEntity.ok(AcademicYearResponse.from(year))
    }

    /**
     * Activates an academic year, archiving any other active year.
     */
    @PutMapping("/{id}/activate")
    fun activate(@PathVariable id: String): ResponseEntity<AcademicYearResponse> {
        val tenantId = TenantContext.requireTenantId()
        val activated = activateAcademicYearUseCase.activate(tenantId, AcademicYearId.of(id))
        return ResponseEntity.ok(AcademicYearResponse.from(activated))
    }

    /**
     * Adds an academic term/semester to an academic year.
     */
    @PostMapping("/{id}/terms")
    fun addTerm(
        @PathVariable id: String,
        @Valid @RequestBody request: AddAcademicTermRequest
    ): ResponseEntity<AcademicTermResponse> {
        val tenantId = TenantContext.requireTenantId()
        val term = addAcademicTermUseCase.addTerm(tenantId, AcademicYearId.of(id), request.toCommand())
        val response = AcademicTermResponse.from(term)
        val location = URI.create("/api/v1/academic-years/$id/terms/${term.id.value}")
        return ResponseEntity.created(location).body(response)
    }

    /**
     * Activates an academic term within an academic year.
     */
    @PutMapping("/{id}/terms/{termId}/activate")
    fun activateTerm(
        @PathVariable id: String,
        @PathVariable termId: String
    ): ResponseEntity<AcademicTermResponse> {
        val tenantId = TenantContext.requireTenantId()
        val term = activateAcademicTermUseCase.activateTerm(tenantId, AcademicYearId.of(id), AcademicTermId.of(termId))
        return ResponseEntity.ok(AcademicTermResponse.from(term))
    }
}
