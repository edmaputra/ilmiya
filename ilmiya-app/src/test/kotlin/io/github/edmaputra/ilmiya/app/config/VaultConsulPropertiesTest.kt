package io.github.edmaputra.ilmiya.app.config

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.runner.ApplicationContextRunner

class VaultConsulPropertiesTest {

    private val contextRunner = ApplicationContextRunner()

    @Test
    fun `should have consul and vault enabled by default`() {
        contextRunner
            .run { context ->
                val consulEnabled = context.environment.getProperty("spring.cloud.consul.enabled", Boolean::class.java, true)
                val vaultEnabled = context.environment.getProperty("spring.cloud.vault.enabled", Boolean::class.java, true)

                assertThat(consulEnabled).isTrue()
                assertThat(vaultEnabled).isTrue()
            }
    }

    @Test
    fun `should allow disabling consul and vault via environment properties`() {
        contextRunner
            .withPropertyValues(
                "spring.cloud.consul.enabled=false",
                "spring.cloud.vault.enabled=false"
            )
            .run { context ->
                val consulEnabled = context.environment.getProperty("spring.cloud.consul.enabled", Boolean::class.java)
                val vaultEnabled = context.environment.getProperty("spring.cloud.vault.enabled", Boolean::class.java)

                assertThat(consulEnabled).isFalse()
                assertThat(vaultEnabled).isFalse()
            }
    }
}
