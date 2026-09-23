package io.github.edmaputra.ilmiya.app.web

import io.github.edmaputra.ilmiya.academic.domain.exception.AcademicTermNotFoundException
import io.github.edmaputra.ilmiya.academic.domain.exception.AcademicYearNotFoundException
import io.github.edmaputra.ilmiya.academic.domain.exception.DuplicateAcademicYearException
import io.github.edmaputra.ilmiya.academic.domain.exception.InvalidAcademicYearDatesException
import io.github.edmaputra.ilmiya.common.exception.DomainException
import io.github.edmaputra.ilmiya.common.exception.MissingTenantContextException
import io.github.edmaputra.ilmiya.common.exception.ResourceNotFoundException
import io.github.edmaputra.ilmiya.core.school.domain.exception.DuplicateInstitutionCodeException
import io.github.edmaputra.ilmiya.core.school.domain.exception.SchoolNotFoundException
import jakarta.servlet.http.HttpServletRequest
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.core.MethodParameter
import org.springframework.http.HttpStatus
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException

class GlobalExceptionHandlerTest {

    private val handler = GlobalExceptionHandler()
    private val request = mockk<HttpServletRequest> {
        every { requestURI } returns "/api/v1/schools"
    }

    @Test
    fun `handleMissingTenantContext should return 400 Bad Request`() {
        val ex = MissingTenantContextException("Tenant header is required")

        val response = handler.handleMissingTenantContext(ex, request)

        assertThat(response.status).isEqualTo(HttpStatus.BAD_REQUEST.value())
        assertThat(response.title).isEqualTo("Missing Tenant Context")
        assertThat(response.detail).isEqualTo("Tenant header is required")
        assertThat(response.instance.toString()).isEqualTo("/api/v1/schools")
    }

    @Test
    fun `handleNotFound should return 404 Not Found for SchoolNotFoundException`() {
        val ex = SchoolNotFoundException("School not found")

        val response = handler.handleNotFound(ex, request)

        assertThat(response.status).isEqualTo(HttpStatus.NOT_FOUND.value())
        assertThat(response.title).isEqualTo("Resource Not Found")
        assertThat(response.detail).isEqualTo("School not found")
    }

    @Test
    fun `handleNotFound should return 404 Not Found for AcademicYearNotFoundException`() {
        val ex = AcademicYearNotFoundException("Academic year not found")

        val response = handler.handleNotFound(ex, request)

        assertThat(response.status).isEqualTo(HttpStatus.NOT_FOUND.value())
        assertThat(response.title).isEqualTo("Resource Not Found")
    }

    @Test
    fun `handleNotFound should return 404 Not Found for AcademicTermNotFoundException`() {
        val ex = AcademicTermNotFoundException("Academic term not found")

        val response = handler.handleNotFound(ex, request)

        assertThat(response.status).isEqualTo(HttpStatus.NOT_FOUND.value())
    }

    @Test
    fun `handleNotFound should return 404 Not Found for ResourceNotFoundException`() {
        val ex = ResourceNotFoundException("School", "sch-01")

        val response = handler.handleNotFound(ex, request)

        assertThat(response.status).isEqualTo(HttpStatus.NOT_FOUND.value())
    }

    @Test
    fun `handleDuplicateResource should return 409 Conflict for DuplicateInstitutionCodeException`() {
        val ex = DuplicateInstitutionCodeException("Duplicate code")

        val response = handler.handleDuplicateResource(ex, request)

        assertThat(response.status).isEqualTo(HttpStatus.CONFLICT.value())
        assertThat(response.title).isEqualTo("Resource Conflict")
        assertThat(response.detail).isEqualTo("Duplicate code")
    }

    @Test
    fun `handleDuplicateResource should return 409 Conflict for DuplicateAcademicYearException`() {
        val ex = DuplicateAcademicYearException("Duplicate academic year")

        val response = handler.handleDuplicateResource(ex, request)

        assertThat(response.status).isEqualTo(HttpStatus.CONFLICT.value())
        assertThat(response.title).isEqualTo("Resource Conflict")
    }

    @Test
    fun `handleBadRequest should return 400 Bad Request for InvalidAcademicYearDatesException`() {
        val ex = InvalidAcademicYearDatesException("End date must be after start date")

        val response = handler.handleBadRequest(ex, request)

        assertThat(response.status).isEqualTo(HttpStatus.BAD_REQUEST.value())
        assertThat(response.title).isEqualTo("Bad Request")
        assertThat(response.detail).isEqualTo("End date must be after start date")
    }

    @Test
    fun `handleBadRequest should return 400 Bad Request for IllegalArgumentException`() {
        val ex = IllegalArgumentException("Invalid argument")

        val response = handler.handleBadRequest(ex, request)

        assertThat(response.status).isEqualTo(HttpStatus.BAD_REQUEST.value())
        assertThat(response.title).isEqualTo("Bad Request")
    }

    @Test
    fun `handleValidationErrors should return 422 Unprocessable Entity with error list`() {
        val bindingResult = BeanPropertyBindingResult(Any(), "target")
        bindingResult.addError(FieldError("target", "name", "name is required"))

        val method = this::class.java.declaredMethods.first()
        val param = MethodParameter(method, -1)
        val ex = MethodArgumentNotValidException(param, bindingResult)

        val response = handler.handleValidationErrors(ex, request)

        assertThat(response.status).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value())
        assertThat(response.title).isEqualTo("Validation Failed")
        assertThat(response.detail).contains("1 validation error")
        assertThat(response.properties?.get("errors")).isNotNull
    }

    @Test
    fun `handleDomainException should return 422 Unprocessable Entity for generic DomainException`() {
        val ex = object : DomainException("Domain business rule failed") {}

        val response = handler.handleDomainException(ex, request)

        assertThat(response.status).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value())
        assertThat(response.title).isEqualTo("Unprocessable Entity")
        assertThat(response.detail).isEqualTo("Domain business rule failed")
    }

    @Test
    fun `handleGeneralException should return 500 Internal Server Error`() {
        val ex = RuntimeException("Unexpected error")

        val response = handler.handleGeneralException(ex, request)

        assertThat(response.status).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value())
        assertThat(response.title).isEqualTo("Internal Server Error")
        assertThat(response.detail).isEqualTo("An unexpected internal error occurred.")
    }
}
