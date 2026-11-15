package io.github.edmaputra.ilmiya.common.context

import io.github.edmaputra.ilmiya.common.domain.TenantId
import io.github.edmaputra.ilmiya.common.exception.MissingTenantContextException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class TenantContextTest {

    @Test
    fun `should bind and retrieve tenant id within scope`() {
        assertThat(TenantContext.isBound()).isFalse()
        assertThat(TenantContext.getTenantId()).isNull()

        val tenantUuid = "00000000-0000-0000-0000-000000000001"
        val result = TenantContext.withTenant(tenantUuid) {
            assertThat(TenantContext.isBound()).isTrue()
            assertThat(TenantContext.getTenantId()).isEqualTo(TenantId.of(tenantUuid))
            assertThat(TenantContext.getTenantIdValue()).isEqualTo(tenantUuid)
            assertThat(TenantContext.requireTenantId()).isEqualTo(TenantId.of(tenantUuid))
            "processed"
        }

        assertThat(result).isEqualTo("processed")
        assertThat(TenantContext.isBound()).isFalse()
        assertThat(TenantContext.getTenantId()).isNull()
    }

    @Test
    fun `should throw MissingTenantContextException when required but not bound`() {
        assertThatThrownBy { TenantContext.requireTenantId() }
            .isInstanceOf(MissingTenantContextException::class.java)
            .hasMessage("A tenant context is required for this operation.")
    }

    @Test
    fun `should execute runnable within tenant scope`() {
        var executed = false
        val tenantUuid = "00000000-0000-0000-0000-000000000002"
        TenantContext.runWithTenant(TenantId.of(tenantUuid)) {
            assertThat(TenantContext.getTenantIdValue()).isEqualTo(tenantUuid)
            executed = true
        }
        assertThat(executed).isTrue()
        assertThat(TenantContext.isBound()).isFalse()
    }
}
