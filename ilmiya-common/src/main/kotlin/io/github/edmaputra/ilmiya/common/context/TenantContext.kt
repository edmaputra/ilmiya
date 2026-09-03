package io.github.edmaputra.ilmiya.common.context

import io.github.edmaputra.ilmiya.common.domain.TenantId
import java.util.Optional

object TenantContext {

    private val CURRENT_TENANT = ThreadLocal<TenantId>()

    fun setTenantId(tenantId: TenantId?) {
        if (tenantId != null) {
            CURRENT_TENANT.set(tenantId)
        } else {
            clear()
        }
    }

    fun setTenantId(tenantId: String?) {
        if (!tenantId.isNullOrBlank()) {
            CURRENT_TENANT.set(TenantId.of(tenantId))
        } else {
            clear()
        }
    }

    fun getTenantId(): Optional<TenantId> = Optional.ofNullable(CURRENT_TENANT.get())

    fun getTenantIdValue(): String? = CURRENT_TENANT.get()?.value

    fun clear() {
        CURRENT_TENANT.remove()
    }
}
