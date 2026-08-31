package com.ilmiya.common.context;

import com.ilmiya.common.domain.TenantId;
import java.util.Optional;

public final class TenantContext {

    private static final ThreadLocal<TenantId> CURRENT_TENANT = new ThreadLocal<>();

    private TenantContext() {
    }

    public static void setTenantId(TenantId tenantId) {
        CURRENT_TENANT.set(tenantId);
    }

    public static void setTenantId(String tenantId) {
        if (tenantId != null && !tenantId.isBlank()) {
            CURRENT_TENANT.set(TenantId.of(tenantId));
        } else {
            clear();
        }
    }

    public static Optional<TenantId> getTenantId() {
        return Optional.ofNullable(CURRENT_TENANT.get());
    }

    public static String getTenantIdValue() {
        TenantId tenant = CURRENT_TENANT.get();
        return tenant != null ? tenant.value() : null;
    }

    public static void clear() {
        CURRENT_TENANT.remove();
    }
}
