package io.github.edmaputra.ilmiya.app.school

import com.ninjasquad.springmockk.MockkBean
import io.github.edmaputra.ilmiya.common.domain.TenantId
import io.github.edmaputra.ilmiya.core.school.adapter.web.SchoolController
import io.github.edmaputra.ilmiya.core.school.domain.School
import io.github.edmaputra.ilmiya.core.school.domain.port.`in`.ConfigureSchoolSettingsUseCase
import io.github.edmaputra.ilmiya.core.school.domain.port.`in`.GetSchoolProfileUseCase
import io.github.edmaputra.ilmiya.core.school.domain.port.`in`.RegisterSchoolUseCase
import io.github.edmaputra.ilmiya.core.school.domain.port.`in`.UpdateSchoolProfileUseCase
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * Controller unit test for school profile management and registration endpoints.
 *
 * @author edmaputra
 * @since 0.0.1
 */
@WebMvcTest(SchoolController::class)
class SchoolControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var registerSchoolUseCase: RegisterSchoolUseCase

    @MockkBean
    private lateinit var getSchoolProfileUseCase: GetSchoolProfileUseCase

    @MockkBean
    private lateinit var updateSchoolProfileUseCase: UpdateSchoolProfileUseCase

    @MockkBean
    private lateinit var configureSchoolSettingsUseCase: ConfigureSchoolSettingsUseCase

    @Test
    fun `should register a new school successfully`() {
        val school = School.create(
            tenantId = TenantId.of("tenant-sch-01"),
            name = "International Islamic School",
            institutionCode = "IIS-01",
            address = "Jl. Pendidikan No. 1",
            contactEmail = "admin@iis.edu",
            contactPhone = "+628123456789"
        )
        every { registerSchoolUseCase.register(any()) } returns school

        mockMvc.perform(
            post("/api/v1/schools")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "tenantId": "tenant-sch-01",
                        "name": "International Islamic School",
                        "institutionCode": "IIS-01",
                        "address": "Jl. Pendidikan No. 1",
                        "contactEmail": "admin@iis.edu",
                        "contactPhone": "+628123456789"
                    }
                    """.trimIndent()
                )
        )
            .andExpect(status().isCreated)
            .andExpect(header().string("Location", "/api/v1/schools/profile"))
            .andExpect(jsonPath("$.institutionCode").value("IIS-01"))
            .andExpect(jsonPath("$.name").value("International Islamic School"))
    }

    @Test
    fun `should get profile when tenant header is supplied`() {
        val tenantId = TenantId.of("tenant-sch-01")
        val school = School.create(
            tenantId = tenantId,
            name = "International Islamic School",
            institutionCode = "IIS-01",
            address = "Jl. Pendidikan No. 1",
            contactEmail = "admin@iis.edu",
            contactPhone = "+628123456789"
        )
        every { getSchoolProfileUseCase.getProfile(tenantId) } returns school

        mockMvc.perform(
            get("/api/v1/schools/profile")
                .header("X-Tenant-ID", "tenant-sch-01")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.tenantId").value("tenant-sch-01"))
    }

    @Test
    fun `should update profile when tenant header and valid body are supplied`() {
        val tenantId = TenantId.of("tenant-sch-01")
        val school = School.create(
            tenantId = tenantId,
            name = "Updated Islamic School",
            institutionCode = "IIS-01",
            address = "Jl. Baru No. 10",
            contactEmail = "info@iis.edu",
            contactPhone = "+62812999999"
        )
        every { updateSchoolProfileUseCase.updateProfile(tenantId, any()) } returns school

        mockMvc.perform(
            put("/api/v1/schools/profile")
                .header("X-Tenant-ID", "tenant-sch-01")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "name": "Updated Islamic School",
                        "address": "Jl. Baru No. 10",
                        "contactEmail": "info@iis.edu",
                        "contactPhone": "+62812999999",
                        "accreditationStatus": "A",
                        "accreditationBody": "BAN-S/M"
                    }
                    """.trimIndent()
                )
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value("Updated Islamic School"))
            .andExpect(jsonPath("$.address").value("Jl. Baru No. 10"))
    }

    @Test
    fun `should configure settings when tenant header and valid body are supplied`() {
        val tenantId = TenantId.of("tenant-sch-01")
        val school = School.create(
            tenantId = tenantId,
            name = "International Islamic School",
            institutionCode = "IIS-01",
            address = "Jl. Pendidikan No. 1",
            contactEmail = "admin@iis.edu",
            contactPhone = "+628123456789"
        )
        every { configureSchoolSettingsUseCase.configureSettings(tenantId, any()) } returns school

        mockMvc.perform(
            put("/api/v1/schools/settings")
                .header("X-Tenant-ID", "tenant-sch-01")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "workingDays": ["MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"],
                        "gradingPreference": "NUMERIC_0_100",
                        "termLayout": "SEMESTER"
                    }
                    """.trimIndent()
                )
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.institutionCode").value("IIS-01"))
    }
}
