package io.github.edmaputra.ilmiya.core.school.application

import io.github.edmaputra.ilmiya.common.domain.TenantId
import io.github.edmaputra.ilmiya.core.school.domain.GradingPreference
import io.github.edmaputra.ilmiya.core.school.domain.School
import io.github.edmaputra.ilmiya.core.school.domain.TermLayout
import io.github.edmaputra.ilmiya.core.school.domain.exception.SchoolNotFoundException
import io.github.edmaputra.ilmiya.core.school.domain.port.`in`.ConfigureSchoolSettingsCommand
import io.github.edmaputra.ilmiya.core.school.domain.port.out.SchoolEventPublisher
import io.github.edmaputra.ilmiya.core.school.domain.port.out.SchoolRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.time.DayOfWeek

class ConfigureSchoolSettingsServiceTest {

    private val repository = mockk<SchoolRepository>()
    private val eventPublisher = mockk<SchoolEventPublisher>(relaxed = true)
    private val service = ConfigureSchoolSettingsService(repository, eventPublisher)

    private val tenantId = TenantId.of("tenant-sch-01")

    @Test
    fun `should successfully configure school settings`() {
        val existingSchool = School.create(
            tenantId = tenantId,
            name = "International Islamic School",
            institutionCode = "IIS-01",
            address = "Jl. Pendidikan No. 1",
            contactEmail = "admin@iis.edu",
            contactPhone = "021-123456"
        )
        existingSchool.pullDomainEvents()

        val command = ConfigureSchoolSettingsCommand(
            workingDays = setOf(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY),
            gradingPreference = GradingPreference.NUMERIC_0_100,
            termLayout = TermLayout.SEMESTER
        )

        every { repository.findByTenantId(tenantId) } returns existingSchool
        every { repository.save(any()) } answers { firstArg() }

        val result = service.configureSettings(tenantId, command)

        assertThat(result.operationalSettings.gradingPreference).isEqualTo(GradingPreference.NUMERIC_0_100)
        assertThat(result.operationalSettings.termLayout).isEqualTo(TermLayout.SEMESTER)
        assertThat(result.operationalSettings.workingDays).containsExactlyInAnyOrder(
            DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY
        )

        verify(exactly = 1) { repository.save(any()) }
        verify(atLeast = 1) { eventPublisher.publishAll(any()) }
    }

    @Test
    fun `should throw SchoolNotFoundException when school does not exist`() {
        val command = ConfigureSchoolSettingsCommand(
            workingDays = setOf(DayOfWeek.MONDAY),
            gradingPreference = GradingPreference.LETTER_A_E,
            termLayout = TermLayout.TRIMESTER
        )

        every { repository.findByTenantId(tenantId) } returns null

        assertThatThrownBy { service.configureSettings(tenantId, command) }
            .isInstanceOf(SchoolNotFoundException::class.java)
            .hasMessageContaining("School profile for tenant 'tenant-sch-01' was not found.")
    }

    @Test
    fun `should throw IllegalArgumentException when command has empty working days`() {
        assertThatThrownBy {
            ConfigureSchoolSettingsCommand(
                workingDays = emptySet(),
                gradingPreference = GradingPreference.LETTER_A_E,
                termLayout = TermLayout.SEMESTER
            )
        }.isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("Working days must not be empty.")
    }
}
