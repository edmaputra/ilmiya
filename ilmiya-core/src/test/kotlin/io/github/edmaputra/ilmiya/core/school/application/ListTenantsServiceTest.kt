package io.github.edmaputra.ilmiya.core.school.application

import io.github.edmaputra.ilmiya.common.domain.TenantId
import io.github.edmaputra.ilmiya.core.school.domain.School
import io.github.edmaputra.ilmiya.core.school.domain.port.out.SchoolRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ListTenantsServiceTest {

    private val schoolRepository = mockk<SchoolRepository>()
    private val service = ListTenantsService(schoolRepository)

    @Test
    fun `should return paginated list of schools with correct metadata`() {
        val school1 = School.create(
            tenantId = TenantId.of("tenant-01"),
            name = "Academy One",
            institutionCode = "AO-01",
            address = "Jl. Satu No. 1",
            contactEmail = "admin@one.edu",
            contactPhone = "021-111111"
        )
        val school2 = School.create(
            tenantId = TenantId.of("tenant-02"),
            name = "Academy Two",
            institutionCode = "AO-02",
            address = "Jl. Dua No. 2",
            contactEmail = "admin@two.edu",
            contactPhone = "021-222222"
        )

        every { schoolRepository.findAll(0, 20) } returns listOf(school1, school2)
        every { schoolRepository.count() } returns 2L

        val result = service.listTenants(page = 0, size = 20)

        assertThat(result.content).hasSize(2)
        assertThat(result.content[0].name).isEqualTo("Academy One")
        assertThat(result.content[1].name).isEqualTo("Academy Two")
        assertThat(result.page).isEqualTo(0)
        assertThat(result.size).isEqualTo(20)
        assertThat(result.totalElements).isEqualTo(2L)
        assertThat(result.totalPages).isEqualTo(1)
        assertThat(result.first).isTrue()
        assertThat(result.last).isTrue()

        verify(exactly = 1) { schoolRepository.findAll(0, 20) }
        verify(exactly = 1) { schoolRepository.count() }
    }

    @Test
    fun `should clamp page size when exceeding maximum of 100`() {
        every { schoolRepository.findAll(0, 100) } returns emptyList()
        every { schoolRepository.count() } returns 0L

        val result = service.listTenants(page = 0, size = 250)

        assertThat(result.size).isEqualTo(100)
        verify(exactly = 1) { schoolRepository.findAll(0, 100) }
    }

    @Test
    fun `should coerce negative page to 0 and non-positive size to 1`() {
        every { schoolRepository.findAll(0, 1) } returns emptyList()
        every { schoolRepository.count() } returns 0L

        val result = service.listTenants(page = -5, size = 0)

        assertThat(result.page).isEqualTo(0)
        assertThat(result.size).isEqualTo(1)
        verify(exactly = 1) { schoolRepository.findAll(0, 1) }
    }

    @Test
    fun `should return empty page when no schools exist`() {
        every { schoolRepository.findAll(0, 20) } returns emptyList()
        every { schoolRepository.count() } returns 0L

        val result = service.listTenants(page = 0, size = 20)

        assertThat(result.content).isEmpty()
        assertThat(result.totalElements).isEqualTo(0L)
        assertThat(result.totalPages).isEqualTo(0)
        assertThat(result.first).isTrue()
        assertThat(result.last).isTrue()
    }
}
