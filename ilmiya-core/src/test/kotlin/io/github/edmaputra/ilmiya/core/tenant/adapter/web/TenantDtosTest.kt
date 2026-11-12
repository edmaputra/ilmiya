package io.github.edmaputra.ilmiya.core.tenant.adapter.web

import io.github.edmaputra.ilmiya.common.domain.TenantId
import io.github.edmaputra.ilmiya.core.tenant.domain.Tenant
import io.github.edmaputra.ilmiya.core.tenant.domain.TenantStatus
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Instant

class TenantDtosTest {

    @Test
    fun `TenantResponse from should correctly map all fields from Tenant domain entity`() {
        val now = Instant.parse("2026-09-26T00:00:00Z")
        val tenant = Tenant(
            id = TenantId.of("tenant-abc"),
            name = "Test Foundation",
            legalEntityNumber = "AHU-999",
            registrationType = "YAYASAN",
            address = "Jl. Merdeka No. 1",
            contactEmail = "contact@foundation.org",
            contactPhone = "+62211234567",
            logoUrl = "https://example.com/logo.png",
            status = TenantStatus.ACTIVE,
            createdAt = now,
            updatedAt = now
        )

        val response = TenantResponse.from(tenant)

        assertThat(response.id).isEqualTo("tenant-abc")
        assertThat(response.tenantId).isEqualTo("tenant-abc")
        assertThat(response.name).isEqualTo("Test Foundation")
        assertThat(response.legalEntityNumber).isEqualTo("AHU-999")
        assertThat(response.registrationType).isEqualTo("YAYASAN")
        assertThat(response.status).isEqualTo("ACTIVE")
        assertThat(response.logoUrl).isEqualTo("https://example.com/logo.png")
        assertThat(response.address).isEqualTo("Jl. Merdeka No. 1")
        assertThat(response.contactEmail).isEqualTo("contact@foundation.org")
        assertThat(response.contactPhone).isEqualTo("+62211234567")
        assertThat(response.createdAt).isEqualTo(now.toString())
        assertThat(response.updatedAt).isEqualTo(now.toString())
    }

    @Test
    fun `CreateTenantRequest toCommand should map fields correctly`() {
        val request = CreateTenantRequest(
            tenantId = "tenant-req-01",
            name = "Request Foundation",
            legalEntityNumber = "LEGAL-01",
            registrationType = "YAYASAN",
            address = "Jl. Request 1",
            contactEmail = "admin@req.org",
            contactPhone = "021-111"
        )
        val cmd = request.toCommand()

        assertThat(cmd.tenantId).isEqualTo("tenant-req-01")
        assertThat(cmd.name).isEqualTo("Request Foundation")
        assertThat(cmd.legalEntityNumber).isEqualTo("LEGAL-01")
        assertThat(cmd.registrationType).isEqualTo("YAYASAN")
        assertThat(cmd.address).isEqualTo("Jl. Request 1")
        assertThat(cmd.contactEmail).isEqualTo("admin@req.org")
        assertThat(cmd.contactPhone).isEqualTo("021-111")
    }

    @Test
    fun `UpdateTenantRequest toCommand should map fields with provided status`() {
        val request = UpdateTenantRequest(
            name = "Updated Request Foundation",
            address = "Jl. Request Baru",
            contactEmail = "info@req.org",
            contactPhone = "021-222",
            status = TenantStatus.SUSPENDED
        )
        val cmd = request.toCommand()

        assertThat(cmd.name).isEqualTo("Updated Request Foundation")
        assertThat(cmd.status).isEqualTo(TenantStatus.SUSPENDED)
    }
}
