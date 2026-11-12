package io.github.edmaputra.ilmiya.core.school.application

import io.github.edmaputra.ilmiya.common.domain.TenantId
import io.github.edmaputra.ilmiya.common.dto.PageResponse
import io.github.edmaputra.ilmiya.core.school.domain.School
import io.github.edmaputra.ilmiya.core.school.domain.port.`in`.ListSchoolsUseCase
import io.github.edmaputra.ilmiya.core.school.domain.port.out.SchoolRepository

/**
 * Application service for listing registered schools with pagination.
 *
 * @property schoolRepository outbound persistence port for school aggregates
 * @author edmaputra
 * @since 0.0.1
 */
class ListSchoolsService(
    private val schoolRepository: SchoolRepository
) : ListSchoolsUseCase {

    override fun listSchools(page: Int, size: Int): PageResponse<School> {
        val totalElements = schoolRepository.count()
        val content = schoolRepository.findAll(page, size)
        return PageResponse.of(
            content = content,
            page = page,
            size = size,
            totalElements = totalElements
        )
    }

    override fun listSchoolsByTenant(tenantId: TenantId, page: Int, size: Int): PageResponse<School> {
        val totalElements = schoolRepository.countByTenantId(tenantId)
        val content = schoolRepository.findAllByTenantId(tenantId, page, size)
        return PageResponse.of(
            content = content,
            page = page,
            size = size,
            totalElements = totalElements
        )
    }
}
