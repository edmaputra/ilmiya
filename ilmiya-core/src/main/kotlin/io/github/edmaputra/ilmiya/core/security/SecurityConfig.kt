package io.github.edmaputra.ilmiya.core.security

import io.github.edmaputra.iam.adapter.security.jwt.JwtAuthenticationFilter
import io.github.edmaputra.iam.domain.security.CurrentActorProvider
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.http.MediaType
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.filter.OncePerRequestFilter

/**
 * Spring Security configuration for Ilmiya.
 *
 * Configures stateless JWT token authentication via ed-iam's [JwtAuthenticationFilter]
 * and establishes endpoint authorization rules.
 *
 * @author edmaputra
 * @since 0.0.1
 */
@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter,
    private val currentActorProvider: CurrentActorProvider
) {

    @Bean
    @Profile("local")
    fun corsConfigurationSource(): org.springframework.web.cors.CorsConfigurationSource {
        val configuration = org.springframework.web.cors.CorsConfiguration()
        configuration.allowedOriginPatterns = listOf("*")
        configuration.allowedMethods = listOf("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS", "HEAD")
        configuration.allowedHeaders = listOf("*")
        configuration.exposedHeaders = listOf("Authorization", "Location", "X-Request-Id", "X-Tenant-Id")
        configuration.allowCredentials = true
        configuration.maxAge = 3600L
        val source = org.springframework.web.cors.UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

    @Bean
    fun securityFilterChain(
        http: HttpSecurity,
        corsConfigurationSource: java.util.Optional<org.springframework.web.cors.CorsConfigurationSource>
    ): SecurityFilterChain {
        if (corsConfigurationSource.isPresent) {
            http.cors { it.configurationSource(corsConfigurationSource.get()) }
        } else {
            http.cors { it.disable() }
        }

        http
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers(
                        // ed-iam authentication endpoints
                        "/api/v1/auth/login",
                        "/api/v1/auth/refresh",
                        "/api/v1/auth/switch-tenant",
                        "/api/v1/auth/mfa/verify",
                        "/api/v1/auth/magic-link/**",
                        // OpenAPI / Swagger endpoints
                        "/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        // Actuator health check
                        "/actuator/**"
                    ).permitAll()
                    .anyRequest().authenticated()
            }
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
            .addFilterAfter(ActorSecurityContextFilter(), JwtAuthenticationFilter::class.java)
            .exceptionHandling { exceptions ->
                exceptions
                    .authenticationEntryPoint { _, response, authException ->
                        response.status = HttpServletResponse.SC_UNAUTHORIZED
                        response.contentType = MediaType.APPLICATION_PROBLEM_JSON_VALUE
                        val msg = authException.message?.replace("\"", "\\\"") ?: "Authentication required"
                        response.writer.write(
                            """{"type":"https://api.edmaputra.github.io/problems/unauthorized","title":"Unauthorized","status":401,"detail":"$msg"}"""
                        )
                    }
                    .accessDeniedHandler { _, response, accessDeniedException ->
                        response.status = HttpServletResponse.SC_FORBIDDEN
                        response.contentType = MediaType.APPLICATION_PROBLEM_JSON_VALUE
                        val msg = accessDeniedException.message?.replace("\"", "\\\"") ?: "Access denied"
                        response.writer.write(
                            """{"type":"https://api.edmaputra.github.io/problems/access-denied","title":"Forbidden","status":403,"detail":"$msg"}"""
                        )
                    }
            }

        return http.build()
    }

    private inner class ActorSecurityContextFilter : OncePerRequestFilter() {
        override fun doFilterInternal(
            request: HttpServletRequest,
            response: HttpServletResponse,
            filterChain: FilterChain
        ) {
            currentActorProvider.currentActor().ifPresent { actor ->
                val authorities = actor.permissions().map { SimpleGrantedAuthority(it) } +
                    actor.roles().map { SimpleGrantedAuthority("ROLE_$it") }
                val auth = UsernamePasswordAuthenticationToken(actor.email(), null, authorities)
                SecurityContextHolder.getContext().authentication = auth
            }
            try {
                filterChain.doFilter(request, response)
            } finally {
                SecurityContextHolder.clearContext()
            }
        }
    }
}
