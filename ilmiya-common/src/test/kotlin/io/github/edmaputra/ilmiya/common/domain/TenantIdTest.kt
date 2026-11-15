package io.github.edmaputra.ilmiya.common.domain

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.util.UUID

class TenantIdTest {

    @Test
    fun `should create valid tenant id with uuid string`() {
        val uuid = UUID.randomUUID().toString()
        val tenantId = TenantId.of(uuid)
        assertThat(tenantId.value).isEqualTo(uuid)
        assertThat(tenantId.toUuid()).isEqualTo(UUID.fromString(uuid))
    }

    @Test
    fun `should create valid tenant id from java UUID`() {
        val javaUuid = UUID.randomUUID()
        val tenantId = TenantId.of(javaUuid)
        assertThat(tenantId.value).isEqualTo(javaUuid.toString())
        assertThat(tenantId.toUuid()).isEqualTo(javaUuid)
    }

    @Test
    fun `should generate random valid tenant id`() {
        val tenantId = TenantId.generate()
        assertThat(tenantId.value).isNotBlank()
        assertThat(runCatching { UUID.fromString(tenantId.value) }.isSuccess).isTrue()
    }

    @Test
    fun `should throw exception when tenant id is blank`() {
        assertThatThrownBy { TenantId.of("   ") }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Tenant ID cannot be blank")
    }

    @Test
    fun `should throw exception when tenant id is not a valid uuid`() {
        assertThatThrownBy { TenantId.of("invalid-custom-id") }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Tenant ID must be a valid UUID: 'invalid-custom-id'")
    }
}
