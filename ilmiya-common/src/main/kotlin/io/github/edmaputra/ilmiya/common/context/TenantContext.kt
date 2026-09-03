package io.github.edmaputra.ilmiya.common.context

import io.github.edmaputra.ilmiya.common.domain.TenantId
import io.github.edmaputra.ilmiya.common.exception.MissingTenantContextException
import java.lang.ScopedValue

/**
 * Scoped-value-backed tenant context accessor and scoping utility.
 * Uses Java 25 ScopedValue for immutable, thread-safe, and virtual-thread-friendly context propagation.
 *
 * @author edmaputra
 */
object TenantContext {

    private val TENANT_ID: ScopedValue<TenantId> = ScopedValue.newInstance()

    /**
     * Returns the currently bound TenantId, or null if unbound.
     */
    fun getTenantId(): TenantId? = if (TENANT_ID.isBound) TENANT_ID.get() else null

    /**
     * Returns the currently bound TenantId as a String, or null if unbound.
     */
    fun getTenantIdValue(): String? = getTenantId()?.value

    /**
     * Checks if a tenant context is currently bound.
     */
    fun isBound(): Boolean = TENANT_ID.isBound

    /**
     * Returns the currently bound TenantId or throws [MissingTenantContextException] if not bound.
     */
    fun requireTenantId(): TenantId =
        getTenantId() ?: throw MissingTenantContextException("A tenant context is required for this operation.")

    /**
     * Executes a block within the scope of the given [tenantId].
     */
    fun <T> withTenant(tenantId: TenantId, block: () -> T): T =
        ScopedValue.where(TENANT_ID, tenantId).call(ScopedValue.CallableOp<T, Exception> { block() })

    /**
     * Executes a block within the scope of the given string [tenantId].
     */
    fun <T> withTenant(tenantId: String, block: () -> T): T =
        withTenant(TenantId.of(tenantId), block)

    /**
     * Executes a runnable within the scope of the given [tenantId].
     */
    fun runWithTenant(tenantId: TenantId, runnable: Runnable) {
        ScopedValue.where(TENANT_ID, tenantId).run(runnable)
    }

    /**
     * Executes a runnable within the scope of the given string [tenantId].
     */
    fun runWithTenant(tenantId: String, runnable: Runnable) {
        runWithTenant(TenantId.of(tenantId), runnable)
    }
}
