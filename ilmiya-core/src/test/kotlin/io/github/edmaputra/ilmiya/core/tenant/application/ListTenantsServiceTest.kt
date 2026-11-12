package io.github.edmaputra.ilmiya.core.tenant.application

import io.github.edmaputra.ilmiya.common.domain.TenantId
import io.github.edmaputra.ilmiya.core.tenant.domain.Tenant
import io.github.edmaputra.ilmiya.core.tenant.domain.port.out.TenantRepository
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ListTenantsServiceTest {

    private val tenantRepository = mockk<TenantRepository>()
    private val service = ListTenantsService(tenantRepository)

    @Test
    fun `listTenants should query repository and return paginated tenants`() {
        val tenant = Tenant.create(
            tenantId = TenantId.of("yayasan-01"),
            name = "Yayasan Pendidikan Al-Azhar",
            address = "Jl. Sisingamangaraja",
            contactEmail = "admin@alazhar.org",
            contactPhone = "021-123456"
        )

        every { tenantRepository.count() } returns 1L
        every { tenantRepository.findAll(0, 10) } returns listOf(tenant)

        val response = service.listTenants(0, 10)

        assertThat(response.content).hasSize(1)
        assertThat(response.content[0].name).isEqualTo("Yayasan Pendidikan Al-Azhar")
        assertThat(response.totalElements).isEqualTo(1L)
        assertThat(response.page).isEqualTo(0)
        assertThat(response.size).isEqualTo(10)
    }
}
