package io.github.edmaputra.ilmiya.app.school

import com.ninjasquad.springmockk.MockkBean
import io.github.edmaputra.ilmiya.common.domain.TenantId
import io.github.edmaputra.ilmiya.common.dto.PageResponse
import io.github.edmaputra.ilmiya.core.school.adapter.web.TenantController
import io.github.edmaputra.ilmiya.core.school.domain.School
import io.github.edmaputra.ilmiya.core.school.domain.SchoolStatus
import io.github.edmaputra.ilmiya.core.school.domain.port.`in`.CreateTenantUseCase
import io.github.edmaputra.ilmiya.core.school.domain.port.`in`.DeleteTenantUseCase
import io.github.edmaputra.ilmiya.core.school.domain.port.`in`.GetTenantUseCase
import io.github.edmaputra.ilmiya.core.school.domain.port.`in`.ListTenantsUseCase
import io.github.edmaputra.ilmiya.core.school.domain.port.`in`.UpdateTenantUseCase
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * Controller unit test for tenant CRUD management endpoints.
 *
 * @author edmaputra
 * @since 0.0.1
 */
@WebMvcTest(TenantController::class)
class TenantControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var listTenantsUseCase: ListTenantsUseCase

    @MockkBean
    private lateinit var getTenantUseCase: GetTenantUseCase

    @MockkBean
    private lateinit var createTenantUseCase: CreateTenantUseCase

    @MockkBean
    private lateinit var updateTenantUseCase: UpdateTenantUseCase

    @MockkBean
    private lateinit var deleteTenantUseCase: DeleteTenantUseCase

    private val tenantId = TenantId.of("sma-nusantara-1")

    private val sampleSchool = School.create(
        tenantId = tenantId,
        name = "SMA Nusantara 1 Jakarta",
        institutionCode = "SMAN-1-JKT",
        address = "Jl. Merdeka No. 45",
        contactEmail = "info@smanusantara1.sch.id",
        contactPhone = "+62-21-5550199"
    )

    @Test
    fun `should list tenants with default pagination parameters`() {
        val pageResponse = PageResponse.of(
            content = listOf(sampleSchool),
            page = 0,
            size = 20,
            totalElements = 1L
        )

        every { listTenantsUseCase.listTenants(0, 20) } returns pageResponse

        mockMvc.perform(get("/api/v1/tenants"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content").isArray)
            .andExpect(jsonPath("$.content[0].tenantId").value("sma-nusantara-1"))
            .andExpect(jsonPath("$.content[0].name").value("SMA Nusantara 1 Jakarta"))
            .andExpect(jsonPath("$.content[0].institutionCode").value("SMAN-1-JKT"))
            .andExpect(jsonPath("$.page").value(0))
            .andExpect(jsonPath("$.size").value(20))
            .andExpect(jsonPath("$.totalElements").value(1))
            .andExpect(jsonPath("$.totalPages").value(1))
            .andExpect(jsonPath("$.first").value(true))
            .andExpect(jsonPath("$.last").value(true))
    }

    @Test
    fun `should get tenant by tenant identifier`() {
        every { getTenantUseCase.getTenant(tenantId) } returns sampleSchool

        mockMvc.perform(get("/api/v1/tenants/sma-nusantara-1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.tenantId").value("sma-nusantara-1"))
            .andExpect(jsonPath("$.name").value("SMA Nusantara 1 Jakarta"))
            .andExpect(jsonPath("$.institutionCode").value("SMAN-1-JKT"))
            .andExpect(jsonPath("$.status").value("ACTIVE"))
    }

    @Test
    fun `should create tenant when request payload is valid`() {
        every { createTenantUseCase.createTenant(any()) } returns sampleSchool

        mockMvc.perform(
            post("/api/v1/tenants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "tenantId": "sma-nusantara-1",
                        "name": "SMA Nusantara 1 Jakarta",
                        "institutionCode": "SMAN-1-JKT",
                        "address": "Jl. Merdeka No. 45",
                        "contactEmail": "info@smanusantara1.sch.id",
                        "contactPhone": "+62-21-5550199"
                    }
                    """.trimIndent()
                )
        )
            .andExpect(status().isCreated)
            .andExpect(header().string("Location", "/api/v1/tenants/sma-nusantara-1"))
            .andExpect(jsonPath("$.tenantId").value("sma-nusantara-1"))
            .andExpect(jsonPath("$.name").value("SMA Nusantara 1 Jakarta"))
    }

    @Test
    fun `should update tenant when payload is valid`() {
        val updatedSchool = sampleSchool.updateProfile(
            name = "SMA Nusantara 1 Jakarta Updated",
            address = "Jl. Merdeka Baru No. 50",
            contactEmail = "admin@smanusantara1.sch.id",
            contactPhone = "+62-21-5550200",
            status = SchoolStatus.ACTIVE
        )
        every { updateTenantUseCase.updateTenant(tenantId, any()) } returns updatedSchool

        mockMvc.perform(
            put("/api/v1/tenants/sma-nusantara-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "name": "SMA Nusantara 1 Jakarta Updated",
                        "address": "Jl. Merdeka Baru No. 50",
                        "contactEmail": "admin@smanusantara1.sch.id",
                        "contactPhone": "+62-21-5550200",
                        "status": "ACTIVE"
                    }
                    """.trimIndent()
                )
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value("SMA Nusantara 1 Jakarta Updated"))
            .andExpect(jsonPath("$.address").value("Jl. Merdeka Baru No. 50"))
    }

    @Test
    fun `should delete tenant by tenant identifier`() {
        every { deleteTenantUseCase.deleteTenant(tenantId) } returns Unit

        mockMvc.perform(delete("/api/v1/tenants/sma-nusantara-1"))
            .andExpect(status().isNoContent)
    }
}
