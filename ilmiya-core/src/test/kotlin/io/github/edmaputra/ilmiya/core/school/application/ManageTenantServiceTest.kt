package io.github.edmaputra.ilmiya.core.school.application

import io.github.edmaputra.ilmiya.common.domain.TenantId
import io.github.edmaputra.ilmiya.core.school.domain.Accreditation
import io.github.edmaputra.ilmiya.core.school.domain.School
import io.github.edmaputra.ilmiya.core.school.domain.SchoolId
import io.github.edmaputra.ilmiya.core.school.domain.SchoolStatus
import io.github.edmaputra.ilmiya.core.school.domain.exception.DuplicateInstitutionCodeException
import io.github.edmaputra.ilmiya.core.school.domain.exception.DuplicateTenantException
import io.github.edmaputra.ilmiya.core.school.domain.exception.TenantNotFoundException
import io.github.edmaputra.ilmiya.core.school.domain.port.`in`.CreateTenantCommand
import io.github.edmaputra.ilmiya.core.school.domain.port.`in`.UpdateTenantCommand
import io.github.edmaputra.ilmiya.core.school.domain.port.out.SchoolEventPublisher
import io.github.edmaputra.ilmiya.core.school.domain.port.out.SchoolRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class ManageTenantServiceTest {

    private val schoolRepository = mockk<SchoolRepository>()
    private val eventPublisher = mockk<SchoolEventPublisher>(relaxed = true)
    private val service = ManageTenantService(schoolRepository, eventPublisher)

    private val tenantId = TenantId.of("tenant-manage-01")

    @Test
    fun `createTenant should validate uniqueness, save school, publish events, and return result`() {
        val command = CreateTenantCommand(
            tenantId = "tenant-manage-01",
            name = "Manage High School",
            institutionCode = "MHS-01",
            address = "Jl. Raya 1",
            contactEmail = "admin@mhs.edu",
            contactPhone = "021-123456"
        )

        every { schoolRepository.existsByInstitutionCode("MHS-01") } returns false
        every { schoolRepository.existsByTenantId(tenantId) } returns false
        every { schoolRepository.save(any()) } answers { firstArg() }

        val created = service.createTenant(command)

        assertThat(created.tenantId).isEqualTo(tenantId)
        assertThat(created.name).isEqualTo("Manage High School")
        assertThat(created.institutionCode).isEqualTo("MHS-01")
        verify(exactly = 1) { schoolRepository.save(any()) }
        verify(exactly = 1) { eventPublisher.publishAll(any()) }
    }

    @Test
    fun `createTenant should throw DuplicateInstitutionCodeException when code exists`() {
        val command = CreateTenantCommand(
            tenantId = "tenant-manage-01",
            name = "Manage High School",
            institutionCode = "mhs-01",
            address = "Jl. Raya 1",
            contactEmail = "admin@mhs.edu",
            contactPhone = "021-123456"
        )
        every { schoolRepository.existsByInstitutionCode("MHS-01") } returns true

        assertThatThrownBy { service.createTenant(command) }
            .isInstanceOf(DuplicateInstitutionCodeException::class.java)
            .hasMessageContaining("A tenant with institution code 'MHS-01' already exists.")
    }

    @Test
    fun `createTenant should throw DuplicateTenantException when tenantId exists`() {
        val command = CreateTenantCommand(
            tenantId = "tenant-manage-01",
            name = "Manage High School",
            institutionCode = "MHS-01",
            address = "Jl. Raya 1",
            contactEmail = "admin@mhs.edu",
            contactPhone = "021-123456"
        )
        every { schoolRepository.existsByInstitutionCode("MHS-01") } returns false
        every { schoolRepository.existsByTenantId(tenantId) } returns true

        assertThatThrownBy { service.createTenant(command) }
            .isInstanceOf(DuplicateTenantException::class.java)
            .hasMessageContaining("A tenant with ID 'tenant-manage-01' already exists.")
    }

    @Test
    fun `getTenant should return school when found`() {
        val school = School.create(
            tenantId = tenantId,
            name = "Manage High School",
            institutionCode = "MHS-01",
            address = "Jl. Raya 1",
            contactEmail = "admin@mhs.edu",
            contactPhone = "021-123456"
        )
        every { schoolRepository.findByTenantId(tenantId) } returns school

        val result = service.getTenant(tenantId)

        assertThat(result.tenantId).isEqualTo(tenantId)
        assertThat(result.name).isEqualTo("Manage High School")
    }

    @Test
    fun `getTenant should throw TenantNotFoundException when not found`() {
        every { schoolRepository.findByTenantId(tenantId) } returns null

        assertThatThrownBy { service.getTenant(tenantId) }
            .isInstanceOf(TenantNotFoundException::class.java)
            .hasMessageContaining("Tenant 'tenant-manage-01' was not found.")
    }

    @Test
    fun `updateTenant should update profile details and status`() {
        val existing = School.create(
            tenantId = tenantId,
            name = "Old School Name",
            institutionCode = "MHS-01",
            address = "Jl. Raya 1",
            contactEmail = "admin@mhs.edu",
            contactPhone = "021-123456"
        )
        existing.pullDomainEvents()

        every { schoolRepository.findByTenantId(tenantId) } returns existing
        every { schoolRepository.save(any()) } answers { firstArg() }

        val command = UpdateTenantCommand(
            name = "Updated School Name",
            address = "Jl. Raya Baru 10",
            contactEmail = "info@mhs.edu",
            contactPhone = "021-999888",
            status = SchoolStatus.INACTIVE
        )

        val updated = service.updateTenant(tenantId, command)

        assertThat(updated.name).isEqualTo("Updated School Name")
        assertThat(updated.address).isEqualTo("Jl. Raya Baru 10")
        assertThat(updated.status).isEqualTo(SchoolStatus.INACTIVE)
        verify(exactly = 1) { schoolRepository.save(any()) }
        verify(exactly = 1) { eventPublisher.publishAll(any()) }
    }

    @Test
    fun `deleteTenant should delete school when it exists`() {
        every { schoolRepository.existsByTenantId(tenantId) } returns true
        every { schoolRepository.deleteByTenantId(tenantId) } returns Unit

        service.deleteTenant(tenantId)

        verify(exactly = 1) { schoolRepository.deleteByTenantId(tenantId) }
    }

    @Test
    fun `deleteTenant should throw TenantNotFoundException when tenant does not exist`() {
        every { schoolRepository.existsByTenantId(tenantId) } returns false

        assertThatThrownBy { service.deleteTenant(tenantId) }
            .isInstanceOf(TenantNotFoundException::class.java)
            .hasMessageContaining("Tenant 'tenant-manage-01' was not found.")
    }
}
