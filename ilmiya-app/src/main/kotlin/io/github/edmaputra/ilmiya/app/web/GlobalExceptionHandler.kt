package io.github.edmaputra.ilmiya.app.web

import io.github.edmaputra.ilmiya.academic.domain.exception.AcademicTermNotFoundException
import io.github.edmaputra.ilmiya.academic.domain.exception.AcademicYearNotFoundException
import io.github.edmaputra.ilmiya.academic.domain.exception.DuplicateAcademicYearException
import io.github.edmaputra.ilmiya.academic.domain.exception.InvalidAcademicYearDatesException
import io.github.edmaputra.ilmiya.common.dto.ApiResponse
import io.github.edmaputra.ilmiya.common.exception.DomainException
import io.github.edmaputra.ilmiya.common.exception.MissingTenantContextException
import io.github.edmaputra.ilmiya.common.exception.ResourceNotFoundException
import io.github.edmaputra.ilmiya.core.school.domain.exception.DuplicateInstitutionCodeException
import io.github.edmaputra.ilmiya.core.school.domain.exception.SchoolNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

/**
 * Global REST exception handler mapping domain and validation exceptions to standard [ApiResponse] envelopes.
 *
 * @author edmaputra
 */
@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(MissingTenantContextException::class)
    fun handleMissingTenantContext(ex: MissingTenantContextException): ResponseEntity<ApiResponse<Nothing>> {
        log.warn("Missing tenant context: {}", ex.message)
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(ex.message ?: "Tenant context is missing"))
    }

    @ExceptionHandler(
        SchoolNotFoundException::class,
        AcademicYearNotFoundException::class,
        AcademicTermNotFoundException::class,
        ResourceNotFoundException::class
    )
    fun handleNotFound(ex: DomainException): ResponseEntity<ApiResponse<Nothing>> {
        log.info("Resource not found: {}", ex.message)
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.error(ex.message ?: "Resource not found"))
    }

    @ExceptionHandler(
        DuplicateInstitutionCodeException::class,
        DuplicateAcademicYearException::class
    )
    fun handleDuplicateResource(ex: DomainException): ResponseEntity<ApiResponse<Nothing>> {
        log.warn("Duplicate resource conflict: {}", ex.message)
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(ApiResponse.error(ex.message ?: "Resource conflict"))
    }

    @ExceptionHandler(
        InvalidAcademicYearDatesException::class,
        IllegalArgumentException::class
    )
    fun handleBadRequest(ex: RuntimeException): ResponseEntity<ApiResponse<Nothing>> {
        log.warn("Bad request: {}", ex.message)
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(ex.message ?: "Invalid request"))
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationErrors(ex: MethodArgumentNotValidException): ResponseEntity<ApiResponse<Nothing>> {
        val errors = ex.bindingResult.fieldErrors.map { fieldError ->
            "${fieldError.field}: ${fieldError.defaultMessage}"
        }
        log.warn("Validation error: {}", errors)
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error("Validation failed", errors))
    }

    @ExceptionHandler(DomainException::class)
    fun handleDomainException(ex: DomainException): ResponseEntity<ApiResponse<Nothing>> {
        log.error("Domain exception: {}", ex.message, ex)
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(ex.message ?: "Business rule violation"))
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneralException(ex: Exception): ResponseEntity<ApiResponse<Nothing>> {
        log.error("Unhandled server exception: {}", ex.message, ex)
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.error("An unexpected internal error occurred"))
    }
}
