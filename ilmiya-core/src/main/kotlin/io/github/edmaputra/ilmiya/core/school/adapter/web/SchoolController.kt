package io.github.edmaputra.ilmiya.core.school.adapter.web

import io.github.edmaputra.iam.domain.security.annotation.RequirePermission
import io.github.edmaputra.ilmiya.common.context.TenantContext
import io.github.edmaputra.ilmiya.common.dto.PageResponse
import io.github.edmaputra.ilmiya.core.school.domain.port.`in`.ConfigureSchoolSettingsUseCase
import io.github.edmaputra.ilmiya.core.school.domain.port.`in`.GetSchoolProfileUseCase
import io.github.edmaputra.ilmiya.core.school.domain.port.`in`.ListTenantsUseCase
import io.github.edmaputra.ilmiya.core.school.domain.port.`in`.RegisterSchoolUseCase
import io.github.edmaputra.ilmiya.core.school.domain.port.`in`.UpdateSchoolProfileUseCase
import io.github.edmaputra.ilmiya.core.security.IlmiyaPermissions
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.net.URI

/**
 * REST controller exposing endpoints for school registration, institutional profiles, and settings.
 *
 * @author edmaputra
 * @since 0.0.1
 */
@RestController
@RequestMapping("/api/v1/schools")
class SchoolController(
    private val registerSchoolUseCase: RegisterSchoolUseCase,
    private val getSchoolProfileUseCase: GetSchoolProfileUseCase,
    private val updateSchoolProfileUseCase: UpdateSchoolProfileUseCase,
    private val configureSchoolSettingsUseCase: ConfigureSchoolSettingsUseCase,
    private val listTenantsUseCase: ListTenantsUseCase
) {

    /**
     * Lists registered schools and tenant boundaries with pagination.
     */
    @GetMapping
    fun listSchools(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<PageResponse<SchoolResponse>> {
        val result = listTenantsUseCase.listTenants(page, size)
        val response = PageResponse(
            content = result.content.map { SchoolResponse.from(it) },
            page = result.page,
            size = result.size,
            totalElements = result.totalElements,
            totalPages = result.totalPages,
            first = result.first,
            last = result.last
        )
        return ResponseEntity.ok(response)
    }

    /**
     * Registers and provisions a new school and tenant boundary.
     */
    @PostMapping
    fun registerSchool(
        @Valid @RequestBody request: RegisterSchoolRequest
    ): ResponseEntity<SchoolResponse> {
        val school = registerSchoolUseCase.register(request.toCommand())
        val response = SchoolResponse.from(school)
        val location = URI.create("/api/v1/schools/profile")
        return ResponseEntity.created(location).body(response)
    }

    /**
     * Retrieves the institutional profile for the currently active tenant context.
     */
    @GetMapping("/profile")
    @RequirePermission(IlmiyaPermissions.SCHOOL_PROFILE_READ)
    fun getProfile(): ResponseEntity<SchoolResponse> {
        val tenantId = TenantContext.requireTenantId()
        val school = getSchoolProfileUseCase.getProfile(tenantId)
        return ResponseEntity.ok(SchoolResponse.from(school))
    }

    /**
     * Updates the school institutional profile for the current tenant.
     */
    @PutMapping("/profile")
    @RequirePermission(IlmiyaPermissions.SCHOOL_PROFILE_WRITE)
    fun updateProfile(
        @Valid @RequestBody request: UpdateSchoolRequest
    ): ResponseEntity<SchoolResponse> {
        val tenantId = TenantContext.requireTenantId()
        val school = updateSchoolProfileUseCase.updateProfile(tenantId, request.toCommand())
        return ResponseEntity.ok(SchoolResponse.from(school))
    }

    /**
     * Configures operational settings for the current school tenant.
     */
    @PutMapping("/settings")
    @RequirePermission(IlmiyaPermissions.SCHOOL_SETTINGS_MANAGE)
    fun configureSettings(
        @Valid @RequestBody request: ConfigureSchoolSettingsRequest
    ): ResponseEntity<SchoolResponse> {
        val tenantId = TenantContext.requireTenantId()
        val school = configureSchoolSettingsUseCase.configureSettings(tenantId, request.toCommand())
        return ResponseEntity.ok(SchoolResponse.from(school))
    }
}
