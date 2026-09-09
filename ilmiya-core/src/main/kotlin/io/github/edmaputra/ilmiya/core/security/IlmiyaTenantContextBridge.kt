package io.github.edmaputra.ilmiya.core.security

import io.github.edmaputra.iam.domain.tenancy.TenantContextBridge
import io.github.edmaputra.ilmiya.common.context.TenantContext
import io.github.edmaputra.ilmiya.common.domain.TenantId
import org.springframework.stereotype.Component
import java.util.UUID

/**
 * Bridge connecting ed-iam's tenancy propagation SPI to Ilmiya's ScopedValue-backed TenantContext.
 *
 * @author edmaputra
 */
@Component
class IlmiyaTenantContextBridge : TenantContextBridge {

    override fun <E : Throwable> runWithTenant(
        tenantId: UUID,
        runnable: TenantContextBridge.ThrowingRunnable<E>
    ) {
        TenantContext.withTenant(TenantId.of(tenantId)) {
            runnable.run()
        }
    }
}
