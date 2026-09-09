package io.github.edmaputra.ilmiya.core.security

import io.github.edmaputra.iam.domain.exception.AccessDeniedException as IamAccessDeniedException
import io.github.edmaputra.iam.domain.exception.AuthenticationException as IamAuthenticationException
import io.github.edmaputra.iam.domain.exception.GroupNotFoundException
import io.github.edmaputra.iam.domain.exception.RoleNotFoundException
import io.github.edmaputra.iam.domain.exception.ScopeNodeNotFoundException
import io.github.edmaputra.iam.domain.exception.UserNotFoundException
import io.github.edmaputra.ilmiya.common.dto.ApiResponse
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException as SpringAccessDeniedException
import org.springframework.security.core.AuthenticationException as SpringAuthenticationException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

/**
 * Controller advice translating IAM domain exceptions and Spring Security exceptions
 * into standard Ilmiya [ApiResponse] envelopes.
 *
 * @author edmaputra
 */
@RestControllerAdvice(basePackages = ["io.github.edmaputra.ilmiya"])
@Order(Ordered.HIGHEST_PRECEDENCE)
class IlmiyaSecurityExceptionHandler {

    @ExceptionHandler(value = [IamAccessDeniedException::class, SpringAccessDeniedException::class])
    fun handleAccessDenied(ex: Exception): ResponseEntity<ApiResponse<Nothing>> {
        val response = ApiResponse.error<Nothing>(
            message = "Access denied: insufficient permissions or out of scope boundary.",
            errors = listOf(ex.message ?: "Access denied")
        )
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response)
    }

    @ExceptionHandler(value = [IamAuthenticationException::class, SpringAuthenticationException::class])
    fun handleAuthentication(ex: Exception): ResponseEntity<ApiResponse<Nothing>> {
        val response = ApiResponse.error<Nothing>(
            message = "Authentication failure.",
            errors = listOf(ex.message ?: "Authentication required")
        )
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response)
    }

    @ExceptionHandler(
        value = [
            UserNotFoundException::class,
            RoleNotFoundException::class,
            GroupNotFoundException::class,
            ScopeNodeNotFoundException::class
        ]
    )
    fun handleEntityNotFound(ex: RuntimeException): ResponseEntity<ApiResponse<Nothing>> {
        val response = ApiResponse.error<Nothing>(
            message = "Resource not found.",
            errors = listOf(ex.message ?: "Requested entity not found")
        )
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response)
    }
}
