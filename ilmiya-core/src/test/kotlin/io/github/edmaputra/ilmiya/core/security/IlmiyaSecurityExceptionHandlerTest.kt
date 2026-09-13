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
    fun `should handle IAM AccessDeniedException with HTTP 403 and ProblemDetail`() {
        val ex = IamAccessDeniedException("User lacks ACADEMIC_YEAR_WRITE permission")
        val problem = handler.handleAccessDenied(ex)

        assertThat(problem.status).isEqualTo(HttpStatus.FORBIDDEN.value())
        assertThat(problem.title).isEqualTo("Access Denied")
        assertThat(problem.detail).contains("User lacks ACADEMIC_YEAR_WRITE permission")
        assertThat(problem.type.toString()).isEqualTo("https://api.edmaputra.github.io/problems/access-denied")
    }

    @Test
    fun `should handle Spring Security AccessDeniedException with HTTP 403 and ProblemDetail`() {
        val ex = SpringAccessDeniedException("Access is denied")
        val problem = handler.handleAccessDenied(ex)

        assertThat(problem.status).isEqualTo(HttpStatus.FORBIDDEN.value())
        assertThat(problem.title).isEqualTo("Access Denied")
        assertThat(problem.detail).contains("Access is denied")
    }

    @Test
    fun `should handle IAM AuthenticationException with HTTP 401 and ProblemDetail`() {
        val ex = IamAuthenticationException("Invalid or expired JWT token")
        val problem = handler.handleAuthentication(ex)

        assertThat(problem.status).isEqualTo(HttpStatus.UNAUTHORIZED.value())
        assertThat(problem.title).isEqualTo("Unauthorized")
        assertThat(problem.detail).contains("Invalid or expired JWT token")
        assertThat(problem.type.toString()).isEqualTo("https://api.edmaputra.github.io/problems/unauthorized")
    }

    @Test
    fun `should handle entity not found exceptions with HTTP 404 and ProblemDetail`() {
        val userEx = UserNotFoundException("user@test.com")
        val userProblem = handler.handleEntityNotFound(userEx)

        assertThat(userProblem.status).isEqualTo(HttpStatus.NOT_FOUND.value())
        assertThat(userProblem.title).isEqualTo("Resource Not Found")
        assertThat(userProblem.detail).contains("User not found with email: user@test.com")
        assertThat(userProblem.type.toString()).isEqualTo("https://api.edmaputra.github.io/problems/not-found")

        val roleEx = RoleNotFoundException("TEACHER")
        val roleProblem = handler.handleEntityNotFound(roleEx)

        assertThat(roleProblem.status).isEqualTo(HttpStatus.NOT_FOUND.value())
        assertThat(roleProblem.title).isEqualTo("Resource Not Found")
        assertThat(roleProblem.detail).contains("Role not found with code: TEACHER")
    }
}
