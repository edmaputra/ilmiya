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
    fun `should have in-cluster endpoints configured by default`() {
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

    @Test
    fun `should configure dev profile with docker compose endpoints`() {
        contextRunner
            .withPropertyValues(
                "spring.cloud.consul.host=localhost",
                "spring.cloud.vault.uri=http://localhost:8200",
                "spring.datasource.url=jdbc:postgresql://localhost:5543/ilmiya_db"
            )
            .run { context ->
                val consulHost = context.environment.getProperty("spring.cloud.consul.host")
                val vaultUri = context.environment.getProperty("spring.cloud.vault.uri")
                val dbUrl = context.environment.getProperty("spring.datasource.url")

                assertThat(consulHost).isEqualTo("localhost")
                assertThat(vaultUri).isEqualTo("http://localhost:8200")
                assertThat(dbUrl).isEqualTo("jdbc:postgresql://localhost:5543/ilmiya_db")
            }
    }

    @Test
    fun `should configure standalone profile with H2 in-memory database and disable consul and vault`() {
        contextRunner
            .withPropertyValues(
                "spring.cloud.consul.enabled=false",
                "spring.cloud.vault.enabled=false",
                "spring.datasource.url=jdbc:h2:mem:ilmiya_db;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH",
                "spring.datasource.driver-class-name=org.h2.Driver"
            )
            .run { context ->
                val consulEnabled = context.environment.getProperty("spring.cloud.consul.enabled", Boolean::class.java)
                val vaultEnabled = context.environment.getProperty("spring.cloud.vault.enabled", Boolean::class.java)
                val dbUrl = context.environment.getProperty("spring.datasource.url")
                val driverClass = context.environment.getProperty("spring.datasource.driver-class-name")

                assertThat(consulEnabled).isFalse()
                assertThat(vaultEnabled).isFalse()
                assertThat(dbUrl).contains("jdbc:h2:mem:ilmiya_db")
                assertThat(driverClass).isEqualTo("org.h2.Driver")
            }
    }
}
