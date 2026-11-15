package io.github.edmaputra.ilmiya.common.domain

import jakarta.validation.constraints.NotBlank
import java.util.UUID

/**
 * Strongly typed tenant identifier value object.
 *
 * @property value the unique tenant key in UUID format
 * @author edmaputra
 * @since 0.0.1
 */
data class TenantId(@NotBlank override val value: String) : EntityId<String> {
    init {
        require(value.isNotBlank()) { "Tenant ID cannot be blank" }
        require(runCatching { UUID.fromString(value) }.isSuccess) {
            "Tenant ID must be a valid UUID: '$value'"
        }
    }

    fun toUuid(): UUID = UUID.fromString(value)

    companion object {
        fun of(value: String): TenantId = TenantId(value)
        fun of(uuid: UUID): TenantId = TenantId(uuid.toString())
        fun generate(): TenantId = TenantId(UUID.randomUUID().toString())
    }
}
