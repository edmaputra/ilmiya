package io.github.edmaputra.ilmiya.academic.adapter.web

import io.github.edmaputra.ilmiya.academic.domain.AcademicTermId
import io.github.edmaputra.ilmiya.academic.domain.AcademicYearId
import io.github.edmaputra.ilmiya.academic.domain.port.`in`.ActivateAcademicTermUseCase
import io.github.edmaputra.ilmiya.academic.domain.port.`in`.ActivateAcademicYearUseCase
import io.github.edmaputra.ilmiya.academic.domain.port.`in`.AddAcademicTermUseCase
import io.github.edmaputra.ilmiya.academic.domain.port.`in`.CreateAcademicYearUseCase
import io.github.edmaputra.ilmiya.academic.domain.port.`in`.GetAcademicYearsUseCase
import io.github.edmaputra.ilmiya.common.context.TenantContext
import io.github.edmaputra.ilmiya.common.dto.ApiResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * REST controller exposing endpoints for academic year and term management.
 *
 * @author edmaputra
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
    ): ResponseEntity<ApiResponse<AcademicYearResponse>> {
        val tenantId = TenantContext.requireTenantId()
        val created = createAcademicYearUseCase.create(tenantId, request.toCommand())
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.ok(AcademicYearResponse.from(created), "Academic year created successfully."))
    }

    /**
     * Lists all academic years for the current tenant.
     */
    @GetMapping
    fun listAcademicYears(): ResponseEntity<ApiResponse<List<AcademicYearResponse>>> {
        val tenantId = TenantContext.requireTenantId()
        val years = getAcademicYearsUseCase.listByTenant(tenantId).map { AcademicYearResponse.from(it) }
        return ResponseEntity.ok(ApiResponse.ok(years))
    }

    /**
     * Retrieves the currently active academic year for the tenant.
     */
    @GetMapping("/active")
    fun getActiveAcademicYear(): ResponseEntity<ApiResponse<AcademicYearResponse?>> {
        val tenantId = TenantContext.requireTenantId()
        val activeYear = getAcademicYearsUseCase.getActiveAcademicYear(tenantId)
        val data = activeYear?.let { AcademicYearResponse.from(it) }
        return ResponseEntity.ok(ApiResponse.ok(data))
    }

    /**
     * Retrieves a single academic year by its ID.
     */
    @GetMapping("/{id}")
    fun getById(@PathVariable id: String): ResponseEntity<ApiResponse<AcademicYearResponse>> {
        val tenantId = TenantContext.requireTenantId()
        val year = getAcademicYearsUseCase.getById(tenantId, AcademicYearId.of(id))
        return ResponseEntity.ok(ApiResponse.ok(AcademicYearResponse.from(year)))
    }

    /**
     * Activates an academic year, archiving any other active year.
     */
    @PutMapping("/{id}/activate")
    fun activate(@PathVariable id: String): ResponseEntity<ApiResponse<AcademicYearResponse>> {
        val tenantId = TenantContext.requireTenantId()
        val activated = activateAcademicYearUseCase.activate(tenantId, AcademicYearId.of(id))
        return ResponseEntity.ok(ApiResponse.ok(AcademicYearResponse.from(activated), "Academic year activated successfully."))
    }

    /**
     * Adds an academic term/semester to an academic year.
     */
    @PostMapping("/{id}/terms")
    fun addTerm(
        @PathVariable id: String,
        @Valid @RequestBody request: AddAcademicTermRequest
    ): ResponseEntity<ApiResponse<AcademicTermResponse>> {
        val tenantId = TenantContext.requireTenantId()
        val term = addAcademicTermUseCase.addTerm(tenantId, AcademicYearId.of(id), request.toCommand())
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.ok(AcademicTermResponse.from(term), "Academic term added successfully."))
    }

    /**
     * Activates an academic term within an academic year.
     */
    @PutMapping("/{id}/terms/{termId}/activate")
    fun activateTerm(
        @PathVariable id: String,
        @PathVariable termId: String
    ): ResponseEntity<ApiResponse<AcademicTermResponse>> {
        val tenantId = TenantContext.requireTenantId()
        val term = activateAcademicTermUseCase.activateTerm(tenantId, AcademicYearId.of(id), AcademicTermId.of(termId))
        return ResponseEntity.ok(ApiResponse.ok(AcademicTermResponse.from(term), "Academic term activated successfully."))
    }
}
