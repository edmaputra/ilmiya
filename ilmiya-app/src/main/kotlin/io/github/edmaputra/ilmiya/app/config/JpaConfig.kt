package io.github.edmaputra.ilmiya.app.config

import org.springframework.boot.persistence.autoconfigure.EntityScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

/**
 * Persistence configuration enabling JPA repository scanning and entity scanning across Ilmiya modules.
 * Isolated from @SpringBootApplication to avoid breaking slice tests (@WebMvcTest).
 *
 * @author edmaputra
 * @since 0.0.1
 */
@Configuration(proxyBeanMethods = false)
@EntityScan(basePackages = ["io.github.edmaputra.ilmiya", "io.github.edmaputra.iam"])
@EnableJpaRepositories(basePackages = ["io.github.edmaputra.ilmiya"])
class JpaConfig
