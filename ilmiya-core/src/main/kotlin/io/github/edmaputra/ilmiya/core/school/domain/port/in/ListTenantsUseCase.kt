package io.github.edmaputra.ilmiya.core.school.domain.port.`in`

import io.github.edmaputra.ilmiya.common.dto.PageResponse
import io.github.edmaputra.ilmiya.core.school.domain.School

/**
 * Primary use case port for listing registered schools and tenant boundaries.
 *
 * @author edmaputra
 * @since 0.0.1
 */
interface ListTenantsUseCase {

    /**
     * Retrieves a paginated list of registered schools and tenant boundaries.
     *
     * @param page zero-based page index
     * @param size page size
     * @return paginated response containing school aggregates
     */
    fun listTenants(page: Int, size: Int): PageResponse<School>
}
