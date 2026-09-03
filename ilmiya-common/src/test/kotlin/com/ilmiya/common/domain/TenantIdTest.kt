package com.ilmiya.common.domain

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class TenantIdTest {

    @Test
    fun `should create valid tenant id`() {
        val tenantId = TenantId.of("tenant-123")
        assertThat(tenantId.value).isEqualTo("tenant-123")
    }

    @Test
    fun `should throw exception when tenant id is blank`() {
        assertThatThrownBy { TenantId.of("   ") }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Tenant ID cannot be blank")
    }
}
