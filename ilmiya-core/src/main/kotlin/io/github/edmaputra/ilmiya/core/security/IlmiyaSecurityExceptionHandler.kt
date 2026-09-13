package io.github.edmaputra.ilmiya.core.security

import io.github.edmaputra.iam.domain.exception.AccessDeniedException as IamAccessDeniedException
import io.github.edmaputra.iam.domain.exception.AuthenticationException as IamAuthenticationException
import io.github.edmaputra.iam.domain.exception.GroupNotFoundException
import io.github.edmaputra.iam.domain.exception.RoleNotFoundException
import io.github.edmaputra.iam.domain.exception.ScopeNodeNotFoundException
import io.github.edmaputra.iam.domain.exception.UserNotFoundException
import jakarta.servlet.http.HttpServletRequest
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.security.access.AccessDeniedException as SpringAccessDeniedException
import org.springframework.security.core.AuthenticationException as SpringAuthenticationException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.net.URI

/**
 * Controller advice translating IAM domain exceptions and Spring Security exceptions
 * into standard RFC 9457 [ProblemDetail] payloads.
 *
 * @author edmaputra
 * @since 0.0.1
 */
@RestControllerAdvice(basePackages = ["io.github.edmaputra.ilmiya"])
@Order(Ordered.HIGHEST_PRECEDENCE)
class IlmiyaSecurityExceptionHandler {

    @ExceptionHandler(value = [IamAccessDeniedException::class, SpringAccessDeniedException::class])
    fun handleAccessDenied(ex: Exception, request: HttpServletRequest? = null): ProblemDetail {
        val detail = ex.message ?: "Access denied: insufficient permissions or out of scope boundary."
        val problem = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, detail)
        problem.title = "Access Denied"
        problem.type = URI.create("https://api.edmaputra.github.io/problems/access-denied")
        if (request != null) {
            problem.instance = URI.create(request.requestURI)
        }
        return problem
    }

    @ExceptionHandler(value = [IamAuthenticationException::class, SpringAuthenticationException::class])
    fun handleAuthentication(ex: Exception, request: HttpServletRequest? = null): ProblemDetail {
        val detail = ex.message ?: "Authentication required"
        val problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, detail)
        problem.title = "Unauthorized"
        problem.type = URI.create("https://api.edmaputra.github.io/problems/unauthorized")
        if (request != null) {
            problem.instance = URI.create(request.requestURI)
        }
        return problem
    }

    @ExceptionHandler(
        value = [
            UserNotFoundException::class,
            RoleNotFoundException::class,
            GroupNotFoundException::class,
            ScopeNodeNotFoundException::class
        ]
    )
    fun handleEntityNotFound(ex: RuntimeException, request: HttpServletRequest? = null): ProblemDetail {
        val detail = ex.message ?: "Requested entity not found"
        val problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, detail)
        problem.title = "Resource Not Found"
        problem.type = URI.create("https://api.edmaputra.github.io/problems/not-found")
        if (request != null) {
            problem.instance = URI.create(request.requestURI)
        }
        return problem
    }
}
