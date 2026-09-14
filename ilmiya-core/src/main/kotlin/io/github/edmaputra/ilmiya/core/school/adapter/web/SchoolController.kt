package io.github.edmaputra.ilmiya.core.school.adapter.web

import io.github.edmaputra.ilmiya.common.context.TenantContext
import io.github.edmaputra.ilmiya.common.dto.ApiResponse
import io.github.edmaputra.ilmiya.core.school.domain.port.`in`.ConfigureSchoolSettingsUseCase
import io.github.edmaputra.ilmiya.core.school.domain.port.`in`.GetSchoolProfileUseCase
import io.github.edmaputra.ilmiya.core.school.domain.port.`in`.RegisterSchoolUseCase
import io.github.edmaputra.ilmiya.core.school.domain.port.`in`.UpdateSchoolProfileUseCase
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * REST controller exposing endpoints for school registration, institutional profiles, and settings.
 *
 * @author edmaputra
 */
@RestController
@RequestMapping("/api/v1/schools")
class SchoolController(
    private val registerSchoolUseCase: RegisterSchoolUseCase,
    private val getSchoolProfileUseCase: GetSchoolProfileUseCase,
    private val updateSchoolProfileUseCase: UpdateSchoolProfileUseCase,
    private val configureSchoolSettingsUseCase: ConfigureSchoolSettingsUseCase
) {

    /**
     * Registers and provisions a new school and tenant boundary.
     */
    @PostMapping
    fun registerSchool(
        @Valid @RequestBody request: RegisterSchoolRequest
    ): ResponseEntity<ApiResponse<SchoolResponse>> {
        val school = registerSchoolUseCase.register(request.toCommand())
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.ok(SchoolResponse.from(school), "School registered successfully."))
    }

    /**
     * Retrieves the institutional profile for the currently active tenant context.
     */
    @GetMapping("/profile")
    fun getProfile(): ResponseEntity<ApiResponse<SchoolResponse>> {
        val tenantId = TenantContext.requireTenantId()
        val school = getSchoolProfileUseCase.getProfile(tenantId)
        return ResponseEntity.ok(ApiResponse.ok(SchoolResponse.from(school)))
    }

    /**
     * Updates the school institutional profile for the current tenant.
     */
    @PutMapping("/profile")
    fun updateProfile(
        @Valid @RequestBody request: UpdateSchoolRequest
    ): ResponseEntity<ApiResponse<SchoolResponse>> {
        val tenantId = TenantContext.requireTenantId()
        val school = updateSchoolProfileUseCase.updateProfile(tenantId, request.toCommand())
        return ResponseEntity.ok(ApiResponse.ok(SchoolResponse.from(school), "School profile updated successfully."))
    }

    /**
     * Configures operational settings for the current school tenant.
     */
    @PutMapping("/settings")
    fun configureSettings(
        @Valid @RequestBody request: ConfigureSchoolSettingsRequest
    ): ResponseEntity<ApiResponse<SchoolResponse>> {
        val tenantId = TenantContext.requireTenantId()
        val school = configureSchoolSettingsUseCase.configureSettings(tenantId, request.toCommand())
        return ResponseEntity.ok(ApiResponse.ok(SchoolResponse.from(school), "Operational settings updated successfully."))
    }
}
