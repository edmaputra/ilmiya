package io.github.edmaputra.ilmiya.core.security

import io.github.edmaputra.iam.adapter.security.jwt.JwtAuthenticationFilter
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

/**
 * Spring Security configuration for Ilmiya.
 *
 * Configures stateless JWT token authentication via ed-iam's [JwtAuthenticationFilter]
 * and establishes endpoint authorization rules.
 *
 * @author edmaputra
 */
@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter
) {

    @Bean
    fun corsConfigurationSource(): org.springframework.web.cors.CorsConfigurationSource {
        val configuration = org.springframework.web.cors.CorsConfiguration()
        configuration.allowedOriginPatterns = listOf("*")
        configuration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
        configuration.allowedHeaders = listOf("*")
        configuration.allowCredentials = true
        val source = org.springframework.web.cors.UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .cors { it.configurationSource(corsConfigurationSource()) }
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers(
                        // ed-iam authentication endpoints
                        "/api/v1/auth/login",
                        "/api/v1/auth/refresh",
                        "/api/v1/auth/switch-tenant",
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
            .exceptionHandling { exceptions ->
                exceptions
                    .authenticationEntryPoint { _, response, authException ->
                        response.status = HttpServletResponse.SC_UNAUTHORIZED
                        response.contentType = MediaType.APPLICATION_JSON_VALUE
                        val msg = authException.message?.replace("\"", "\\\"") ?: "Authentication required"
                        response.writer.write(
                            """{"success":false,"message":"Unauthorized","errors":["$msg"]}"""
                        )
                    }
                    .accessDeniedHandler { _, response, accessDeniedException ->
                        response.status = HttpServletResponse.SC_FORBIDDEN
                        response.contentType = MediaType.APPLICATION_JSON_VALUE
                        val msg = accessDeniedException.message?.replace("\"", "\\\"") ?: "Access denied"
                        response.writer.write(
                            """{"success":false,"message":"Access Denied","errors":["$msg"]}"""
                        )
                    }
            }

        return http.build()
    }
}
