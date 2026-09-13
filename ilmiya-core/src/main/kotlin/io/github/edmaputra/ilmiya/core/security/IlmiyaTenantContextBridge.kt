package io.github.edmaputra.ilmiya.core.security

import io.github.edmaputra.iam.domain.tenancy.TenantContextBridge
import io.github.edmaputra.ilmiya.common.context.TenantContext
import io.github.edmaputra.ilmiya.common.domain.TenantId
import org.slf4j.MDC
import org.springframework.stereotype.Component
import java.util.UUID

/**
 * Bridge connecting ed-iam's tenancy propagation SPI to Ilmiya's ScopedValue-backed TenantContext.
 * Also populates MDC tenantId for structured logging.
 *
 * @author edmaputra
 * @since 0.0.1
 */
@Component
class IlmiyaTenantContextBridge : TenantContextBridge {

    override fun <E : Throwable> runWithTenant(
        tenantId: UUID,
        runnable: TenantContextBridge.ThrowingRunnable<E>
    ) {
        MDC.put("tenantId", tenantId.toString())
        try {
            TenantContext.withTenant(TenantId.of(tenantId)) {
                runnable.run()
            }
        } finally {
            MDC.remove("tenantId")
        }
    }
}
