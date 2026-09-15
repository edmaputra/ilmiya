package io.github.edmaputra.ilmiya.core.school.application

import io.github.edmaputra.ilmiya.common.domain.TenantId
import io.github.edmaputra.ilmiya.core.school.domain.School
import io.github.edmaputra.ilmiya.core.school.domain.exception.SchoolNotFoundException
import io.github.edmaputra.ilmiya.core.school.domain.port.`in`.UpdateSchoolProfileCommand
import io.github.edmaputra.ilmiya.core.school.domain.port.out.SchoolEventPublisher
import io.github.edmaputra.ilmiya.core.school.domain.port.out.SchoolRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class SchoolProfileServiceTest {

    private val repository = mockk<SchoolRepository>()
    private val eventPublisher = mockk<SchoolEventPublisher>(relaxed = true)
    private val service = SchoolProfileService(repository, eventPublisher)
    private val tenantId = TenantId.of("tenant-sch-01")

    @Test
    fun `should return profile when found`() {
        val school = School.create(
            tenantId = tenantId,
            name = "SMA 1",
            institutionCode = "SMA1",
            address = "Jl. Sudirman",
            contactEmail = "admin@sma1.sch.id",
            contactPhone = "021-999"
        )
        every { repository.findByTenantId(tenantId) } returns school

        val result = service.getProfile(tenantId)

        assertThat(result.name).isEqualTo("SMA 1")
    }

    @Test
    fun `should throw SchoolNotFoundException when school not found`() {
        every { repository.findByTenantId(tenantId) } returns null

        assertThatThrownBy { service.getProfile(tenantId) }
            .isInstanceOf(SchoolNotFoundException::class.java)
    }

    @Test
    fun `should update profile and publish events`() {
        val school = School.create(
            tenantId = tenantId,
            name = "SMA 1",
            institutionCode = "SMA1",
            address = "Jl. Sudirman",
            contactEmail = "admin@sma1.sch.id",
            contactPhone = "021-999"
        )
        every { repository.findByTenantId(tenantId) } returns school
        every { repository.save(any<School>()) } answers { firstArg() }

        val command = UpdateSchoolProfileCommand(
            name = "SMA 1 Unggulan",
            address = "Jl. Thamrin",
            contactEmail = "new@sma1.sch.id",
            contactPhone = "021-888"
        )

        val updated = service.updateProfile(tenantId, command)

        assertThat(updated.name).isEqualTo("SMA 1 Unggulan")
        assertThat(updated.address).isEqualTo("Jl. Thamrin")
        verify(exactly = 1) { repository.save(any<School>()) }
        verify(atLeast = 1) { eventPublisher.publishAll(any()) }
    }
}
