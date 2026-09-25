package io.github.edmaputra.ilmiya.core.school.adapter.web

import io.github.edmaputra.ilmiya.common.domain.TenantId
import io.github.edmaputra.ilmiya.core.school.domain.Accreditation
import io.github.edmaputra.ilmiya.core.school.domain.GradingPreference
import io.github.edmaputra.ilmiya.core.school.domain.OperationalSettings
import io.github.edmaputra.ilmiya.core.school.domain.School
import io.github.edmaputra.ilmiya.core.school.domain.SchoolId
import io.github.edmaputra.ilmiya.core.school.domain.SchoolStatus
import io.github.edmaputra.ilmiya.core.school.domain.TermLayout
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate

class TenantDtosTest {

    @Test
    fun `TenantResponse from should correctly map all fields from School domain entity`() {
        val now = Instant.parse("2026-09-26T00:00:00Z")
        val school = School(
            id = SchoolId.of("sch-123"),
            tenantId = TenantId.of("tenant-abc"),
            name = "Test International School",
            institutionCode = "TIS-01",
            governmentRegistrationNumber = "GOV-999",
            registrationType = "NPSN",
            address = "Jl. Merdeka No. 1",
            contactEmail = "contact@tis.edu",
            contactPhone = "+62211234567",
            logoUrl = "https://example.com/logo.png",
            accreditation = Accreditation(
                status = "A",
                accreditingBody = "BAN-S/M",
                validUntil = LocalDate.of(2030, 1, 1)
            ),
            operationalSettings = OperationalSettings(
                workingDays = setOf(DayOfWeek.MONDAY, DayOfWeek.TUESDAY),
                gradingPreference = GradingPreference.NUMERIC_0_100,
                termLayout = TermLayout.SEMESTER
            ),
            status = SchoolStatus.ACTIVE,
            createdAt = now,
            updatedAt = now
        )

        val response = TenantResponse.from(school)

        assertThat(response.id).isEqualTo("sch-123")
        assertThat(response.tenantId).isEqualTo("tenant-abc")
        assertThat(response.name).isEqualTo("Test International School")
        assertThat(response.institutionCode).isEqualTo("TIS-01")
        assertThat(response.status).isEqualTo("ACTIVE")
        assertThat(response.logoUrl).isEqualTo("https://example.com/logo.png")
        assertThat(response.address).isEqualTo("Jl. Merdeka No. 1")
        assertThat(response.contactEmail).isEqualTo("contact@tis.edu")
        assertThat(response.contactPhone).isEqualTo("+62211234567")
        assertThat(response.governmentRegistrationNumber).isEqualTo("GOV-999")
        assertThat(response.registrationType).isEqualTo("NPSN")
        assertThat(response.createdAt).isEqualTo(now.toString())
        assertThat(response.updatedAt).isEqualTo(now.toString())
    }

    @Test
    fun `CreateTenantRequest toCommand should map fields and set default operational settings`() {
        val request = CreateTenantRequest(
            tenantId = "tenant-req-01",
            name = "Request School",
            institutionCode = "RS-01",
            address = "Jl. Request 1",
            contactEmail = "admin@req.edu",
            contactPhone = "021-111"
        )
        val cmd = request.toCommand()

        assertThat(cmd.tenantId).isEqualTo("tenant-req-01")
        assertThat(cmd.name).isEqualTo("Request School")
        assertThat(cmd.institutionCode).isEqualTo("RS-01")
        assertThat(cmd.operationalSettings.gradingPreference).isEqualTo(GradingPreference.NUMERIC_0_100)
        assertThat(cmd.operationalSettings.termLayout).isEqualTo(TermLayout.SEMESTER)
        assertThat(cmd.operationalSettings.workingDays).contains(DayOfWeek.MONDAY, DayOfWeek.FRIDAY)
    }

    @Test
    fun `UpdateTenantRequest toCommand should map fields with provided status`() {
        val request = UpdateTenantRequest(
            name = "Updated Request School",
            address = "Jl. Request Baru",
            contactEmail = "info@req.edu",
            contactPhone = "021-222",
            status = SchoolStatus.SUSPENDED
        )
        val cmd = request.toCommand()

        assertThat(cmd.name).isEqualTo("Updated Request School")
        assertThat(cmd.status).isEqualTo(SchoolStatus.SUSPENDED)
    }
}
