package io.github.edmaputra.ilmiya.core.tenant.adapter.web

import io.github.edmaputra.iam.domain.security.annotation.RequirePermission
import io.github.edmaputra.ilmiya.common.domain.TenantId
import io.github.edmaputra.ilmiya.common.dto.PageResponse
import io.github.edmaputra.ilmiya.core.security.IlmiyaPermissions
import io.github.edmaputra.ilmiya.core.tenant.domain.port.`in`.CreateTenantUseCase
import io.github.edmaputra.ilmiya.core.tenant.domain.port.`in`.DeleteTenantUseCase
import io.github.edmaputra.ilmiya.core.tenant.domain.port.`in`.GetTenantUseCase
import io.github.edmaputra.ilmiya.core.tenant.domain.port.`in`.ListTenantsUseCase
import io.github.edmaputra.ilmiya.core.tenant.domain.port.`in`.UpdateTenantUseCase
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.net.URI

/**
 * REST controller exposing CRUD endpoints for managing registered tenant foundations across the platform.
 *
 * @author edmaputra
 * @since 0.0.1
 */
@RestController
@RequestMapping("/api/v1/tenants")
class TenantController(
    private val listTenantsUseCase: ListTenantsUseCase,
    private val getTenantUseCase: GetTenantUseCase,
    private val createTenantUseCase: CreateTenantUseCase,
    private val updateTenantUseCase: UpdateTenantUseCase,
    private val deleteTenantUseCase: DeleteTenantUseCase
) {

    /**
     * Lists registered tenant foundations with pagination.
     */
    @GetMapping
    @RequirePermission(IlmiyaPermissions.TENANT_READ)
    fun listTenants(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<PageResponse<TenantResponse>> {
        val result = listTenantsUseCase.listTenants(page, size)
        val response = PageResponse(
            content = result.content.map { TenantResponse.from(it) },
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
     * Retrieves a single tenant foundation by its identifier.
     */
    @GetMapping("/{tenantId}")
    @RequirePermission(IlmiyaPermissions.TENANT_READ)
    fun getTenant(
        @PathVariable tenantId: String
    ): ResponseEntity<TenantResponse> {
        val tenant = getTenantUseCase.getTenant(TenantId.of(tenantId))
        return ResponseEntity.ok(TenantResponse.from(tenant))
    }

    /**
     * Creates and provisions a new tenant foundation boundary.
     */
    @PostMapping
    @RequirePermission(IlmiyaPermissions.TENANT_CREATE)
    fun createTenant(
        @Valid @RequestBody request: CreateTenantRequest
    ): ResponseEntity<TenantResponse> {
        val tenant = createTenantUseCase.createTenant(request.toCommand())
        val response = TenantResponse.from(tenant)
        val location = URI.create("/api/v1/tenants/${tenant.id.value}")
        return ResponseEntity.created(location).body(response)
    }

    /**
     * Updates an existing tenant foundation profile.
     */
    @PutMapping("/{tenantId}")
    @RequirePermission(IlmiyaPermissions.TENANT_UPDATE)
    fun updateTenant(
        @PathVariable tenantId: String,
        @Valid @RequestBody request: UpdateTenantRequest
    ): ResponseEntity<TenantResponse> {
        val tenant = updateTenantUseCase.updateTenant(TenantId.of(tenantId), request.toCommand())
        return ResponseEntity.ok(TenantResponse.from(tenant))
    }

    /**
     * Deletes a tenant foundation boundary.
     */
    @DeleteMapping("/{tenantId}")
    @RequirePermission(IlmiyaPermissions.TENANT_DELETE)
    fun deleteTenant(
        @PathVariable tenantId: String
    ): ResponseEntity<Void> {
        deleteTenantUseCase.deleteTenant(TenantId.of(tenantId))
        return ResponseEntity.noContent().build()
    }
}
