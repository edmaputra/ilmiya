package com.ilmiya.common.context

import com.ilmiya.common.domain.TenantId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

class TenantContextTest {

    @AfterEach
    fun tearDown() {
        TenantContext.clear()
    }

    @Test
    fun `should set and get tenant id`() {
        TenantContext.setTenantId("school-1")

        assertThat(TenantContext.getTenantId()).isPresent
        assertThat(TenantContext.getTenantId().get()).isEqualTo(TenantId.of("school-1"))
        assertThat(TenantContext.getTenantIdValue()).isEqualTo("school-1")
    }

    @Test
    fun `should clear tenant id when set with blank or null`() {
        TenantContext.setTenantId("school-1")
        TenantContext.setTenantId("")

        assertThat(TenantContext.getTenantId()).isEmpty
        assertThat(TenantContext.getTenantIdValue()).isNull()
    }
}
