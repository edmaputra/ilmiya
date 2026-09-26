package io.github.edmaputra.ilmiya.core.school.application

import io.github.edmaputra.ilmiya.common.dto.PageResponse
import io.github.edmaputra.ilmiya.core.school.domain.School
import io.github.edmaputra.ilmiya.core.school.domain.port.`in`.ListTenantsUseCase
import io.github.edmaputra.ilmiya.core.school.domain.port.out.SchoolRepository

/**
 * Application service implementing [ListTenantsUseCase].
 *
 * @property schoolRepository outbound persistence port for school entities
 * @author edmaputra
 * @since 0.0.1
 */
class ListTenantsService(
    private val schoolRepository: SchoolRepository
) : ListTenantsUseCase {

    override fun listTenants(page: Int, size: Int): PageResponse<School> {
        val safePage = page.coerceAtLeast(0)
        val clampedSize = size.coerceIn(1, 100)
        val content = schoolRepository.findAll(safePage, clampedSize)
        val totalElements = schoolRepository.count()
        return PageResponse.of(
            content = content,
            page = safePage,
            size = clampedSize,
            totalElements = totalElements
        )
    }
}
