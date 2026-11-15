package io.github.edmaputra.ilmiya.app.config

import io.swagger.v3.oas.models.security.SecurityScheme
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.info.BuildProperties
import java.util.Properties

class OpenApiConfigTest {

    private val openApiConfig = OpenApiConfig()

    @Test
    fun `should configure OpenAPI with JWT bearer authentication and default version`() {
        val openAPI = openApiConfig.customOpenAPI()

        assertThat(openAPI.info).isNotNull
        assertThat(openAPI.info.title).isEqualTo("Ilmiya API")
        assertThat(openAPI.info.version).isEqualTo(OpenApiConfig.DEFAULT_VERSION)

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

    @Test
    fun `should use version from build properties when available`() {
        val properties = Properties().apply {
            setProperty("version", "1.2.3")
        }
        val buildProperties = BuildProperties(properties)
        val configWithBuildProperties = OpenApiConfig(buildProperties)

        val openAPI = configWithBuildProperties.customOpenAPI()

        assertThat(openAPI.info.version).isEqualTo("1.2.3")
    }
}
