package com.ilmiya.common.domain;

import jakarta.validation.constraints.NotBlank;
import java.util.Objects;

public record TenantId(@NotBlank String value) implements EntityId<String> {
    public TenantId {
        Objects.requireNonNull(value, "Tenant ID cannot be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("Tenant ID cannot be blank");
        }
    }

    public static TenantId of(String value) {
        return new TenantId(value);
    }
}
