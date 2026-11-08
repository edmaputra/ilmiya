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

    @Test
    fun `should allow configuring k8s cluster endpoints via environment properties`() {
        contextRunner
            .withPropertyValues(
                "spring.cloud.consul.host=consul-server",
                "spring.cloud.vault.uri=http://vault:8200",
                "spring.datasource.url=jdbc:postgresql://postgres:5432/ilmiya_db"
            )
            .run { context ->
                val consulHost = context.environment.getProperty("spring.cloud.consul.host")
                val vaultUri = context.environment.getProperty("spring.cloud.vault.uri")
                val dbUrl = context.environment.getProperty("spring.datasource.url")

                assertThat(consulHost).isEqualTo("consul-server")
                assertThat(vaultUri).isEqualTo("http://vault:8200")
                assertThat(dbUrl).isEqualTo("jdbc:postgresql://postgres:5432/ilmiya_db")
            }
    }
}
