package io.github.edmaputra.ilmiya.core.security

import io.github.edmaputra.iam.domain.exception.AccessDeniedException as IamAccessDeniedException
import io.github.edmaputra.iam.domain.exception.AuthenticationException as IamAuthenticationException
import io.github.edmaputra.iam.domain.exception.RoleNotFoundException
import io.github.edmaputra.iam.domain.exception.UserNotFoundException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.security.access.AccessDeniedException as SpringAccessDeniedException

class IlmiyaSecurityExceptionHandlerTest {

    private val handler = IlmiyaSecurityExceptionHandler()

    @Test
    fun `should handle IAM AccessDeniedException with HTTP 403 and error ApiResponse`() {
        val ex = IamAccessDeniedException("User lacks ACADEMIC_YEAR_WRITE permission")
        val response = handler.handleAccessDenied(ex)

        assertThat(response.statusCode).isEqualTo(HttpStatus.FORBIDDEN)
        assertThat(response.body).isNotNull
        assertThat(response.body!!.success).isFalse()
        assertThat(response.body!!.message).contains("Access denied")
        assertThat(response.body!!.errors).contains("User lacks ACADEMIC_YEAR_WRITE permission")
    }

    @Test
    fun `should handle Spring Security AccessDeniedException with HTTP 403`() {
        val ex = SpringAccessDeniedException("Access is denied")
        val response = handler.handleAccessDenied(ex)

        assertThat(response.statusCode).isEqualTo(HttpStatus.FORBIDDEN)
        assertThat(response.body!!.success).isFalse()
        assertThat(response.body!!.errors).contains("Access is denied")
    }

    @Test
    fun `should handle IAM AuthenticationException with HTTP 401 and error ApiResponse`() {
        val ex = IamAuthenticationException("Invalid or expired JWT token")
        val response = handler.handleAuthentication(ex)

        assertThat(response.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
        assertThat(response.body).isNotNull
        assertThat(response.body!!.success).isFalse()
        assertThat(response.body!!.message).contains("Authentication failure")
        assertThat(response.body!!.errors).contains("Invalid or expired JWT token")
    }

    @Test
    fun `should handle entity not found exceptions with HTTP 404`() {
        val userEx = UserNotFoundException("user@test.com")
        val userResponse = handler.handleEntityNotFound(userEx)

        assertThat(userResponse.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
        assertThat(userResponse.body!!.success).isFalse()
        assertThat(userResponse.body!!.errors).contains("User not found with email: user@test.com")

        val roleEx = RoleNotFoundException("TEACHER")
        val roleResponse = handler.handleEntityNotFound(roleEx)

        assertThat(roleResponse.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
        assertThat(roleResponse.body!!.errors).contains("Role not found with code: TEACHER")
    }
}
