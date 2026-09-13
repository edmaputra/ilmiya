package io.github.edmaputra.ilmiya.common.domain

import jakarta.validation.constraints.NotBlank

/**
 * Strongly typed tenant identifier value object.
 *
 * @property value the unique tenant key
 * @author edmaputra
 * @since 0.0.1
 */
data class TenantId(@NotBlank override val value: String) : EntityId<String> {
    init {
        require(value.isNotBlank()) { "Tenant ID cannot be blank" }
    }

    fun toUuid(): java.util.UUID = java.util.UUID.fromString(value)

    companion object {
        fun of(value: String): TenantId = TenantId(value)
        fun of(uuid: java.util.UUID): TenantId = TenantId(uuid.toString())
    }
}
