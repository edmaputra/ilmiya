package io.github.edmaputra.ilmiya.academic.domain

import io.github.edmaputra.ilmiya.common.domain.EntityId
import java.util.UUID

/**
 * Strongly typed identifier for an academic term entity.
 *
 * @property value unique academic term identifier string
 * @author edmaputra
 */
data class AcademicTermId(override val value: String) : EntityId<String> {
    init {
        require(value.isNotBlank()) { "Academic Term ID must not be blank." }
    }

    companion object {
        fun generate(): AcademicTermId = AcademicTermId(UUID.randomUUID().toString())
        fun of(value: String): AcademicTermId = AcademicTermId(value)
    }
}
