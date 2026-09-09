package io.github.edmaputra.ilmiya.core.security

import io.github.edmaputra.iam.adapter.security.jwt.JwtAuthenticationFilter
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

    @Test
    fun `should register SecurityFilterChain bean containing JwtAuthenticationFilter`() {
        contextRunner.run { context ->
            assertThat(context).hasNotFailed()
            assertThat(context).hasSingleBean(SecurityFilterChain::class.java)
            val chain = context.getBean(SecurityFilterChain::class.java)
            assertThat(chain.filters.any { it is JwtAuthenticationFilter }).isTrue()
        }
    }
}
