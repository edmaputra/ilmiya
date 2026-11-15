package io.github.edmaputra.ilmiya.app.filter

import io.github.edmaputra.ilmiya.common.context.TenantContext
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse

class TenantContextFilterTest {

    private val filter = TenantContextFilter()

    @Test
    fun `should bind tenant context when header is present`() {
        val request = MockHttpServletRequest()
        request.addHeader("X-Tenant-ID", "00000000-0000-0000-0000-000000000001")
        val response = MockHttpServletResponse()

        var contextBoundInsideChain = false
        var boundTenantId: String? = null

        val filterChain = FilterChain { _, _ ->
            contextBoundInsideChain = TenantContext.isBound()
            boundTenantId = TenantContext.getTenantIdValue()
        }

        filter.doFilter(request, response, filterChain)

        assertThat(contextBoundInsideChain).isTrue()
        assertThat(boundTenantId).isEqualTo("00000000-0000-0000-0000-000000000001")
        // Once exited, context should be unbound
        assertThat(TenantContext.isBound()).isFalse()
    }

    @Test
    fun `should not bind tenant context when header is absent`() {
        val request = MockHttpServletRequest()
        val response = MockHttpServletResponse()

        var contextBoundInsideChain = false

        val filterChain = FilterChain { _, _ ->
            contextBoundInsideChain = TenantContext.isBound()
        }

        filter.doFilter(request, response, filterChain)

        assertThat(contextBoundInsideChain).isFalse()
        assertThat(TenantContext.isBound()).isFalse()
    }
}
