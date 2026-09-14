package io.github.edmaputra.ilmiya.core.school.domain

import io.github.edmaputra.ilmiya.common.domain.EntityId
import java.util.UUID

/**
 * Strongly typed identifier for a school institution aggregate.
 *
 * @property value the unique school ID string
 * @author edmaputra
 */
data class SchoolId(override val value: String) : EntityId<String> {
    init {
        require(value.isNotBlank()) { "School ID must not be blank." }
    }

    companion object {
        fun generate(): SchoolId = SchoolId(UUID.randomUUID().toString())
        fun of(value: String): SchoolId = SchoolId(value)
    }
}
