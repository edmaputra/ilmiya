package io.github.edmaputra.ilmiya.common.domain

import jakarta.validation.constraints.NotBlank

data class TenantId(@NotBlank override val value: String) : EntityId<String> {
    init {
        require(value.isNotBlank()) { "Tenant ID cannot be blank" }
    }

    companion object {
        fun of(value: String): TenantId = TenantId(value)
    }
}
