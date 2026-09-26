package io.github.edmaputra.ilmiya.app.web

import io.github.edmaputra.ilmiya.academic.domain.exception.AcademicTermNotFoundException
import io.github.edmaputra.ilmiya.academic.domain.exception.AcademicYearNotFoundException
import io.github.edmaputra.ilmiya.academic.domain.exception.DuplicateAcademicYearException
import io.github.edmaputra.ilmiya.academic.domain.exception.InvalidAcademicYearDatesException
import io.github.edmaputra.ilmiya.common.exception.DomainException
import io.github.edmaputra.ilmiya.common.exception.MissingTenantContextException
import io.github.edmaputra.ilmiya.common.exception.ResourceNotFoundException
import io.github.edmaputra.ilmiya.core.school.domain.exception.DuplicateInstitutionCodeException
import io.github.edmaputra.ilmiya.core.school.domain.exception.DuplicateTenantException
import io.github.edmaputra.ilmiya.core.school.domain.exception.SchoolNotFoundException
import io.github.edmaputra.ilmiya.core.school.domain.exception.TenantNotFoundException
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.net.URI

/**
 * Global REST exception handler mapping domain and validation exceptions to RFC 9457 [ProblemDetail] payloads.
 *
 * @author edmaputra
 * @since 0.0.1
 */
@RestControllerAdvice(basePackages = ["io.github.edmaputra.ilmiya"])
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(MissingTenantContextException::class)
    fun handleMissingTenantContext(
        ex: MissingTenantContextException,
        request: HttpServletRequest
    ): ProblemDetail {
        log.warn("Missing tenant context: {}", ex.message)
        val problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            ex.message ?: "Tenant context is missing"
        )
        problem.title = "Missing Tenant Context"
        problem.type = URI.create("https://api.edmaputra.github.io/problems/missing-tenant-context")
        problem.instance = URI.create(request.requestURI)
        return problem
    }

    @ExceptionHandler(
        SchoolNotFoundException::class,
        TenantNotFoundException::class,
        AcademicYearNotFoundException::class,
        AcademicTermNotFoundException::class,
        ResourceNotFoundException::class
    )
    fun handleNotFound(ex: DomainException, request: HttpServletRequest): ProblemDetail {
        log.info("Resource not found: {}", ex.message)
        val problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.NOT_FOUND,
            ex.message ?: "Resource not found"
        )
        problem.title = "Resource Not Found"
        problem.type = URI.create("https://api.edmaputra.github.io/problems/not-found")
        problem.instance = URI.create(request.requestURI)
        return problem
    }

    @ExceptionHandler(
        DuplicateInstitutionCodeException::class,
        DuplicateTenantException::class,
        DuplicateAcademicYearException::class
    )
    fun handleDuplicateResource(ex: DomainException, request: HttpServletRequest): ProblemDetail {
        log.warn("Duplicate resource conflict: {}", ex.message)
        val problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.CONFLICT,
            ex.message ?: "Resource conflict"
        )
        problem.title = "Resource Conflict"
        problem.type = URI.create("https://api.edmaputra.github.io/problems/conflict")
        problem.instance = URI.create(request.requestURI)
        return problem
    }

    @ExceptionHandler(
        InvalidAcademicYearDatesException::class,
        IllegalArgumentException::class
    )
    fun handleBadRequest(ex: RuntimeException, request: HttpServletRequest): ProblemDetail {
        log.warn("Bad request: {}", ex.message)
        val problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            ex.message ?: "Invalid request syntax or parameters"
        )
        problem.title = "Bad Request"
        problem.type = URI.create("https://api.edmaputra.github.io/problems/bad-request")
        problem.instance = URI.create(request.requestURI)
        return problem
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationErrors(
        ex: MethodArgumentNotValidException,
        request: HttpServletRequest
    ): ProblemDetail {
        val fieldErrors = ex.bindingResult.fieldErrors.map { fe ->
            mapOf(
                "field" to fe.field,
                "message" to (fe.defaultMessage ?: "Invalid value"),
                "rejectedValue" to (fe.rejectedValue ?: "null")
            )
        }
        log.warn("Validation error: {}", fieldErrors)
        val problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.UNPROCESSABLE_ENTITY,
            "Request contains ${fieldErrors.size} validation error(s)."
        )
        problem.title = "Validation Failed"
        problem.type = URI.create("https://api.edmaputra.github.io/problems/validation-error")
        problem.instance = URI.create(request.requestURI)
        problem.setProperty("errors", fieldErrors)
        return problem
    }

    @ExceptionHandler(DomainException::class)
    fun handleDomainException(ex: DomainException, request: HttpServletRequest): ProblemDetail {
        log.error("Domain exception: {}", ex.message, ex)
        val problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.UNPROCESSABLE_ENTITY,
            ex.message ?: "Business rule violation"
        )
        problem.title = "Unprocessable Entity"
        problem.type = URI.create("https://api.edmaputra.github.io/problems/unprocessable-entity")
        problem.instance = URI.create(request.requestURI)
        return problem
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneralException(ex: Exception, request: HttpServletRequest): ProblemDetail {
        log.error("Unhandled server exception: {}", ex.message, ex)
        val problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "An unexpected internal error occurred."
        )
        problem.title = "Internal Server Error"
        problem.type = URI.create("https://api.edmaputra.github.io/problems/internal-server-error")
        problem.instance = URI.create(request.requestURI)
        return problem
    }
}
