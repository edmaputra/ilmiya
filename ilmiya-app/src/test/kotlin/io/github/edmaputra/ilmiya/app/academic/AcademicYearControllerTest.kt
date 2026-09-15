package io.github.edmaputra.ilmiya.app.academic

import com.ninjasquad.springmockk.MockkBean
import io.github.edmaputra.ilmiya.academic.adapter.web.AcademicYearController
import io.github.edmaputra.ilmiya.academic.domain.AcademicTerm
import io.github.edmaputra.ilmiya.academic.domain.AcademicTermId
import io.github.edmaputra.ilmiya.academic.domain.AcademicTermType
import io.github.edmaputra.ilmiya.academic.domain.AcademicYear
import io.github.edmaputra.ilmiya.academic.domain.AcademicYearId
import io.github.edmaputra.ilmiya.academic.domain.port.`in`.ActivateAcademicTermUseCase
import io.github.edmaputra.ilmiya.academic.domain.port.`in`.ActivateAcademicYearUseCase
import io.github.edmaputra.ilmiya.academic.domain.port.`in`.AddAcademicTermUseCase
import io.github.edmaputra.ilmiya.academic.domain.port.`in`.CreateAcademicYearUseCase
import io.github.edmaputra.ilmiya.academic.domain.port.`in`.GetAcademicYearsUseCase
import io.github.edmaputra.ilmiya.common.domain.TenantId
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDate

/**
 * Unit tests for AcademicYearController REST endpoints.
 *
 * @author edmaputra
 * @since 0.0.1
 */
@WebMvcTest(AcademicYearController::class)
class AcademicYearControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var createAcademicYearUseCase: CreateAcademicYearUseCase

    @MockkBean
    private lateinit var activateAcademicYearUseCase: ActivateAcademicYearUseCase

    @MockkBean
    private lateinit var getAcademicYearsUseCase: GetAcademicYearsUseCase

    @MockkBean
    private lateinit var addAcademicTermUseCase: AddAcademicTermUseCase

    @MockkBean
    private lateinit var activateAcademicTermUseCase: ActivateAcademicTermUseCase

    private val tenantId = TenantId.of("tenant-sch-01")

    @Test
    fun `should create academic year when payload is valid`() {
        val created = AcademicYear.create(
            id = AcademicYearId.of("year-123"),
            tenantId = tenantId,
            name = "2026/2027",
            startDate = LocalDate.of(2026, 7, 1),
            endDate = LocalDate.of(2027, 6, 30)
        )
        every { createAcademicYearUseCase.create(tenantId, any()) } returns created

        mockMvc.perform(
            post("/api/v1/academic-years")
                .header("X-Tenant-ID", "tenant-sch-01")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "name": "2026/2027",
                        "startDate": "2026-07-01",
                        "endDate": "2027-06-30"
                    }
                    """.trimIndent()
                )
        )
            .andExpect(status().isCreated)
            .andExpect(header().string("Location", "/api/v1/academic-years/year-123"))
            .andExpect(jsonPath("$.name").value("2026/2027"))
            .andExpect(jsonPath("$.status").value("PLANNED"))
    }

    @Test
    fun `should add term to academic year`() {
        val term = AcademicTerm(
            id = AcademicTermId.of("term-1"),
            name = "Semester 1",
            type = AcademicTermType.SEMESTER_1,
            displayName = "Semester Ganjil",
            startDate = LocalDate.of(2026, 7, 15),
            endDate = LocalDate.of(2026, 12, 20)
        )
        every { addAcademicTermUseCase.addTerm(tenantId, AcademicYearId.of("year-123"), any()) } returns term

        mockMvc.perform(
            post("/api/v1/academic-years/year-123/terms")
                .header("X-Tenant-ID", "tenant-sch-01")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "name": "Semester 1",
                        "type": "SEMESTER_1",
                        "displayName": "Semester Ganjil",
                        "startDate": "2026-07-15",
                        "endDate": "2026-12-20"
                    }
                    """.trimIndent()
                )
        )
            .andExpect(status().isCreated)
            .andExpect(header().string("Location", "/api/v1/academic-years/year-123/terms/term-1"))
            .andExpect(jsonPath("$.displayName").value("Semester Ganjil"))
    }

    @Test
    fun `should list academic years for current tenant`() {
        val year = AcademicYear.create(
            id = AcademicYearId.of("year-123"),
            tenantId = tenantId,
            name = "2026/2027",
            startDate = LocalDate.of(2026, 7, 1),
            endDate = LocalDate.of(2027, 6, 30)
        )
        every { getAcademicYearsUseCase.listByTenant(tenantId) } returns listOf(year)

        mockMvc.perform(
            get("/api/v1/academic-years")
                .header("X-Tenant-ID", "tenant-sch-01")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].name").value("2026/2027"))
    }
}
