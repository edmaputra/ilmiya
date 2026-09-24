package io.github.edmaputra.ilmiya.app.config

import io.swagger.v3.oas.models.security.SecurityScheme
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class OpenApiConfigTest {

    private val openApiConfig = OpenApiConfig()

    @Test
    fun `should configure OpenAPI with JWT bearer authentication`() {
        val openAPI = openApiConfig.customOpenAPI()

        assertThat(openAPI.info).isNotNull
        assertThat(openAPI.info.title).isEqualTo("Ilmiya API")
        assertThat(openAPI.info.version).isEqualTo("0.0.1")

        assertThat(openAPI.security).hasSize(1)
        val securityRequirement = openAPI.security[0]
        assertThat(securityRequirement.keys).contains(OpenApiConfig.SECURITY_SCHEME_NAME)

        assertThat(openAPI.components).isNotNull
        val securityScheme = openAPI.components.securitySchemes[OpenApiConfig.SECURITY_SCHEME_NAME]
        assertThat(securityScheme).isNotNull
        assertThat(securityScheme?.type).isEqualTo(SecurityScheme.Type.HTTP)
        assertThat(securityScheme?.scheme).isEqualTo("bearer")
        assertThat(securityScheme?.bearerFormat).isEqualTo("JWT")
    }
}
