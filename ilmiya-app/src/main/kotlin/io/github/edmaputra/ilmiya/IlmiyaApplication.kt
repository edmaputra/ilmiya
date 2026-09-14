package io.github.edmaputra.ilmiya

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

/**
 * Spring Boot composition root for Ilmiya application.
 *
 * @author edmaputra
 * @since 0.0.1
 */
@SpringBootApplication
@ConfigurationPropertiesScan
class IlmiyaApplication

fun main(args: Array<String>) {
    runApplication<IlmiyaApplication>(*args)
}
