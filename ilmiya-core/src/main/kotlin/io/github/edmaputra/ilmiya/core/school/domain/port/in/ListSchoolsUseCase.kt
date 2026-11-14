package io.github.edmaputra.ilmiya.core.school.domain.port.`in`

import io.github.edmaputra.ilmiya.common.domain.TenantId
import io.github.edmaputra.ilmiya.common.dto.PageResponse
import io.github.edmaputra.ilmiya.core.school.domain.School

/**
 * Inbound port contract for listing registered schools with pagination,
 * either globally or scoped to a specific tenant foundation.
 *
 * @author edmaputra
 * @since 0.0.1
 */
interface ListSchoolsUseCase {

    /**
     * Lists all schools across the platform with pagination.
     */
    fun listSchools(page: Int, size: Int): PageResponse<School>

    /**
     * Lists all schools belonging to a specific tenant foundation with pagination.
     */
    fun listSchoolsByTenant(tenantId: TenantId, page: Int, size: Int): PageResponse<School>
}
