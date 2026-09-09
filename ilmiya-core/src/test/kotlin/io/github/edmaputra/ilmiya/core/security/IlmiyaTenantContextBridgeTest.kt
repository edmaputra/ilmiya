package io.github.edmaputra.ilmiya.core.security

import io.github.edmaputra.iam.domain.tenancy.TenantContextBridge
import io.github.edmaputra.ilmiya.common.context.TenantContext
import io.github.edmaputra.ilmiya.common.domain.TenantId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.UUID

class IlmiyaTenantContextBridgeTest {

    private val bridge = IlmiyaTenantContextBridge()

    @Test
    fun `should bind tenant id into TenantContext within runnable execution`() {
        val tenantUuid = UUID.randomUUID()

        assertThat(TenantContext.isBound()).isFalse()

        var executed = false
        bridge.runWithTenant(tenantUuid, TenantContextBridge.ThrowingRunnable {
            assertThat(TenantContext.isBound()).isTrue()
            assertThat(TenantContext.requireTenantId()).isEqualTo(TenantId.of(tenantUuid))
            assertThat(TenantContext.getTenantIdValue()).isEqualTo(tenantUuid.toString())
            executed = true
        })

        assertThat(executed).isTrue()
        assertThat(TenantContext.isBound()).isFalse()
    }
}
