package com.ilmiya.common.domain

import jakarta.validation.constraints.NotBlank

@JvmInline
value class TenantId(@NotBlank override val value: String) : EntityId<String> {
    init {
        require(value.isNotBlank()) { "Tenant ID cannot be blank" }
    }

    companion object {
        fun of(value: String): TenantId = TenantId(value)
    }
}
