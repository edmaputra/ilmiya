package io.github.edmaputra.ilmiya.app.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * OpenAPI 3 / Swagger UI configuration for Ilmiya.
 *
 * Configures JWT Bearer token authentication in Swagger UI so that requests
 * can include the Authorization header automatically.
 *
 * @author edmaputra
 * @since 0.0.1
 */
@Configuration(proxyBeanMethods = false)
class OpenApiConfig {

    companion object {
        const val SECURITY_SCHEME_NAME = "BearerAuth"
    }

    @Bean
    fun customOpenAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("Ilmiya API")
                    .description("API documentation for Ilmiya School Management System")
                    .version("0.0.1")
            )
            .addSecurityItem(SecurityRequirement().addList(SECURITY_SCHEME_NAME))
            .components(
                Components().addSecuritySchemes(
                    SECURITY_SCHEME_NAME,
                    SecurityScheme()
                        .name(SECURITY_SCHEME_NAME)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("Provide the JWT bearer token for authorization.")
                )
            )
    }
}
