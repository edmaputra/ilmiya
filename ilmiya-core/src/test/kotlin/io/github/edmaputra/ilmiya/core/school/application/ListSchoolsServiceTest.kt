package io.github.edmaputra.ilmiya.core.school.application

import io.github.edmaputra.ilmiya.common.domain.TenantId
import io.github.edmaputra.ilmiya.core.school.domain.School
import io.github.edmaputra.ilmiya.core.school.domain.port.out.SchoolRepository
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ListSchoolsServiceTest {

    private val schoolRepository = mockk<SchoolRepository>()
    private val service = ListSchoolsService(schoolRepository)

    private val tenantId = TenantId.of("yayasan-01")
    private val school = School.create(
        tenantId = tenantId,
        name = "SMA Islam Al-Azhar 1",
        institutionCode = "SMALAZ-01",
        address = "Jl. Sisingamangaraja",
        contactEmail = "sma@alazhar.sch.id",
        contactPhone = "021-123456"
    )

    @Test
    fun `listSchools should return all schools paginated`() {
        every { schoolRepository.count() } returns 1L
        every { schoolRepository.findAll(0, 10) } returns listOf(school)

        val result = service.listSchools(0, 10)

        assertThat(result.content).hasSize(1)
        assertThat(result.content[0].name).isEqualTo("SMA Islam Al-Azhar 1")
        assertThat(result.totalElements).isEqualTo(1L)
    }

    @Test
    fun `listSchoolsByTenant should return schools scoped to tenant paginated`() {
        every { schoolRepository.countByTenantId(tenantId) } returns 1L
        every { schoolRepository.findAllByTenantId(tenantId, 0, 10) } returns listOf(school)

        val result = service.listSchoolsByTenant(tenantId, 0, 10)

        assertThat(result.content).hasSize(1)
        assertThat(result.content[0].name).isEqualTo("SMA Islam Al-Azhar 1")
        assertThat(result.totalElements).isEqualTo(1L)
    }
}
