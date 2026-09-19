package io.github.edmaputra.ilmiya.core.security

import io.github.edmaputra.iam.adapter.security.jwt.JwtAuthenticationFilter
import io.github.edmaputra.iam.domain.security.CurrentActorProvider
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration
import org.springframework.boot.security.autoconfigure.web.servlet.SecurityFilterAutoConfiguration
import org.springframework.boot.test.context.runner.WebApplicationContextRunner
import org.springframework.security.web.SecurityFilterChain

class SecurityConfigTest {

    private val contextRunner = WebApplicationContextRunner()
        .withConfiguration(
            AutoConfigurations.of(
                SecurityAutoConfiguration::class.java,
                SecurityFilterAutoConfiguration::class.java
            )
        )
        .withUserConfiguration(SecurityConfig::class.java)
        .withBean(JwtAuthenticationFilter::class.java, { mock(JwtAuthenticationFilter::class.java) })
        .withBean(CurrentActorProvider::class.java, { mock(CurrentActorProvider::class.java) })

    @Test
    fun `should register SecurityFilterChain bean containing JwtAuthenticationFilter`() {
        contextRunner.run { context ->
            assertThat(context).hasNotFailed()
            assertThat(context).hasSingleBean(SecurityFilterChain::class.java)
            val chain = context.getBean(SecurityFilterChain::class.java)
            assertThat(chain.filters.any { it is JwtAuthenticationFilter }).isTrue()
            assertThat(chain.filters.any { it.javaClass.simpleName == "ActorSecurityContextFilter" }).isTrue()
        }
    }

    @Test
    fun `should not register CorsConfigurationSource bean when profile is not local`() {
        contextRunner.run { context ->
            assertThat(context).doesNotHaveBean(org.springframework.web.cors.CorsConfigurationSource::class.java)
        }
    }

    @Test
    fun `should register CorsConfigurationSource bean with expected origins, methods, and headers when local profile is active`() {
        contextRunner.withPropertyValues("spring.profiles.active=local").run { context ->
            assertThat(context).hasSingleBean(org.springframework.web.cors.CorsConfigurationSource::class.java)
            val source = context.getBean(org.springframework.web.cors.CorsConfigurationSource::class.java)
            val request = org.springframework.mock.web.MockHttpServletRequest("GET", "/api/v1/schools")
            val config = source.getCorsConfiguration(request)
            assertThat(config).isNotNull
            assertThat(config?.allowCredentials).isTrue()
            assertThat(config?.allowedOriginPatterns).containsExactly("*")
            assertThat(config?.allowedMethods).contains("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS", "HEAD")
            assertThat(config?.allowedHeaders).containsExactly("*")
            assertThat(config?.exposedHeaders).contains("Authorization", "Location", "X-Request-Id", "X-Tenant-Id")
        }
    }
}
