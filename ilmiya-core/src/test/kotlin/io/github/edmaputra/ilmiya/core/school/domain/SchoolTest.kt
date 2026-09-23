package io.github.edmaputra.ilmiya.core.school.domain

import io.github.edmaputra.ilmiya.common.domain.TenantId
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.time.DayOfWeek
import java.time.LocalDate

class SchoolTest {

    private val tenantId = TenantId.of("tenant-sch-01")

    @Test
    fun `should successfully create a valid school aggregate`() {
        val school = School.create(
            tenantId = tenantId,
            name = "International Islamic School",
            institutionCode = "iis-01",
            governmentRegistrationNumber = "NPSN12345678",
            registrationType = "NPSN",
            address = "Jl. Pendidikan No. 1",
            contactEmail = "admin@iis.edu",
            contactPhone = "+628123456789",
            accreditation = Accreditation(status = "A", accreditingBody = "BAN-S/M", validUntil = LocalDate.of(2030, 12, 31))
        )

        assertThat(school.id.value).isNotBlank()
        assertThat(school.tenantId).isEqualTo(tenantId)
        assertThat(school.name).isEqualTo("International Islamic School")
        assertThat(school.institutionCode).isEqualTo("IIS-01")
        assertThat(school.governmentRegistrationNumber).isEqualTo("NPSN12345678")
        assertThat(school.status).isEqualTo(SchoolStatus.ACTIVE)

        val events = school.pullDomainEvents()
        assertThat(events).hasSize(1)
        assertThat(events.first()).isInstanceOf(SchoolRegisteredEvent::class.java)
    }

    @Test
    fun `should throw exception when school name is blank`() {
        assertThatThrownBy {
            School.create(
                tenantId = tenantId,
                name = "   ",
                institutionCode = "IIS-01",
                address = "Jl. Pendidikan No. 1",
                contactEmail = "admin@iis.edu",
                contactPhone = "+628123456789"
            )
        }.isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("School name must not be blank")
    }

    @Test
    fun `should update profile and register domain event`() {
        val school = School.create(
            tenantId = tenantId,
            name = "International Islamic School",
            institutionCode = "IIS-01",
            address = "Jl. Pendidikan No. 1",
            contactEmail = "admin@iis.edu",
            contactPhone = "+628123456789"
        )
        school.pullDomainEvents() // Clear initial register event

        val updated = school.updateProfile(
            name = "Global Islamic Academy",
            address = "Jl. Baru No. 10",
            contactEmail = "info@gia.edu",
            contactPhone = "+62899999999",
            accreditation = Accreditation(status = "A")
        )

        assertThat(updated.name).isEqualTo("Global Islamic Academy")
        assertThat(updated.address).isEqualTo("Jl. Baru No. 10")
        assertThat(updated.contactEmail).isEqualTo("info@gia.edu")

        val events = updated.pullDomainEvents()
        assertThat(events).hasSize(1)
        assertThat(events.first()).isInstanceOf(SchoolProfileUpdatedEvent::class.java)
    }

    @Test
    fun `should configure settings and register domain event`() {
        val school = School.create(
            tenantId = tenantId,
            name = "International Islamic School",
            institutionCode = "IIS-01",
            address = "Jl. Pendidikan No. 1",
            contactEmail = "admin@iis.edu",
            contactPhone = "+628123456789"
        )
        school.pullDomainEvents()

        val newSettings = OperationalSettings(
            workingDays = setOf(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY),
            gradingPreference = GradingPreference.LETTER_A_E,
            termLayout = TermLayout.TRIMESTER
        )

        val updated = school.configureSettings(newSettings)
        assertThat(updated.operationalSettings.workingDays).hasSize(6)
        assertThat(updated.operationalSettings.gradingPreference).isEqualTo(GradingPreference.LETTER_A_E)
        assertThat(updated.operationalSettings.termLayout).isEqualTo(TermLayout.TRIMESTER)

        val events = updated.pullDomainEvents()
        assertThat(events).hasSize(1)
        assertThat(events.first()).isInstanceOf(SchoolSettingsConfiguredEvent::class.java)
    }

    @Test
    fun `should correctly populate auditableFields map`() {
        val school = School.create(
            tenantId = tenantId,
            name = "International Islamic School",
            institutionCode = "IIS-01",
            governmentRegistrationNumber = "NPSN123",
            registrationType = "NPSN",
            address = "Jl. Pendidikan No. 1",
            contactEmail = "admin@iis.edu",
            contactPhone = "+628123456789",
            accreditation = Accreditation(status = "A")
        )

        val auditable = school.auditableFields()
        assertThat(auditable["schoolId"]).isEqualTo(school.id.value)
        assertThat(auditable["tenantId"]).isEqualTo(tenantId.value)
        assertThat(auditable["name"]).isEqualTo("International Islamic School")
        assertThat(auditable["institutionCode"]).isEqualTo("IIS-01")
        assertThat(auditable["governmentRegistrationNumber"]).isEqualTo("NPSN123")
        assertThat(auditable["registrationType"]).isEqualTo("NPSN")
        assertThat(auditable["address"]).isEqualTo("Jl. Pendidikan No. 1")
        assertThat(auditable["contactEmail"]).isEqualTo("admin@iis.edu")
        assertThat(auditable["contactPhone"]).isEqualTo("+628123456789")
        assertThat(auditable["accreditationStatus"]).isEqualTo("A")
        assertThat(auditable["status"]).isEqualTo("ACTIVE")
    }

    @Test
    fun `should throw exception when institution code is blank`() {
        assertThatThrownBy {
            School.create(
                tenantId = tenantId,
                name = "School",
                institutionCode = "   ",
                address = "Jl. 1",
                contactEmail = "admin@iis.edu",
                contactPhone = "123"
            )
        }.isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("Institution code must not be blank")
    }

    @Test
    fun `should throw exception when address is blank`() {
        assertThatThrownBy {
            School.create(
                tenantId = tenantId,
                name = "School",
                institutionCode = "SCH",
                address = "   ",
                contactEmail = "admin@iis.edu",
                contactPhone = "123"
            )
        }.isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("School address must not be blank")
    }

    @Test
    fun `should throw exception when contact email is blank`() {
        assertThatThrownBy {
            School.create(
                tenantId = tenantId,
                name = "School",
                institutionCode = "SCH",
                address = "Jl. 1",
                contactEmail = "   ",
                contactPhone = "123"
            )
        }.isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("Contact email must not be blank")
    }

    @Test
    fun `should throw exception when contact phone is blank`() {
        assertThatThrownBy {
            School.create(
                tenantId = tenantId,
                name = "School",
                institutionCode = "SCH",
                address = "Jl. 1",
                contactEmail = "admin@iis.edu",
                contactPhone = "   "
            )
        }.isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("Contact phone must not be blank")
    }
}
