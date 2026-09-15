package io.github.edmaputra.ilmiya.academic.domain

import io.github.edmaputra.ilmiya.common.domain.EntityId
import java.util.UUID

/**
 * Strongly typed identifier for an academic year aggregate.
 *
 * @property value unique academic year identifier string
 * @author edmaputra
 * @since 0.0.1
 */
data class AcademicYearId(override val value: String) : EntityId<String> {
    init {
        require(value.isNotBlank()) { "Academic Year ID must not be blank." }
    }

    companion object {
        fun generate(): AcademicYearId = AcademicYearId(UUID.randomUUID().toString())
        fun of(value: String): AcademicYearId = AcademicYearId(value)
    }
}
