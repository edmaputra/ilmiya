package io.github.edmaputra.ilmiya.app.filter

import io.github.edmaputra.ilmiya.common.context.TenantContext
import io.github.edmaputra.ilmiya.common.domain.TenantId
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

/**
 * Filter that inspects incoming HTTP requests for the multi-tenant discriminator header (`X-Tenant-ID`)
 * and binds it to the Java 25 [TenantContext] scoped value across the downstream filter chain and handler execution.
 *
 * @author edmaputra
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class TenantContextFilter : OncePerRequestFilter() {

    companion object {
        const val TENANT_HEADER = "X-Tenant-ID"
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val tenantHeaderValue = request.getHeader(TENANT_HEADER)
        if (!tenantHeaderValue.isNullOrBlank()) {
            TenantContext.withTenant(TenantId.of(tenantHeaderValue.trim())) {
                filterChain.doFilter(request, response)
            }
        } else {
            filterChain.doFilter(request, response)
        }
    }
}
