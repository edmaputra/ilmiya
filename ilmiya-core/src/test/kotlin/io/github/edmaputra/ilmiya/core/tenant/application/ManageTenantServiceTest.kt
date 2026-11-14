package io.github.edmaputra.ilmiya.core.tenant.application

import io.github.edmaputra.ilmiya.common.domain.TenantId
import io.github.edmaputra.ilmiya.core.tenant.domain.Tenant
import io.github.edmaputra.ilmiya.core.tenant.domain.TenantStatus
import io.github.edmaputra.ilmiya.core.tenant.domain.exception.DuplicateTenantException
import io.github.edmaputra.ilmiya.core.tenant.domain.exception.TenantNotFoundException
import io.github.edmaputra.ilmiya.core.tenant.domain.port.`in`.CreateTenantCommand
import io.github.edmaputra.ilmiya.core.tenant.domain.port.`in`.UpdateTenantCommand
import io.github.edmaputra.ilmiya.core.tenant.domain.port.out.TenantEventPublisher
import io.github.edmaputra.ilmiya.core.tenant.domain.port.out.TenantRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class ManageTenantServiceTest {

    private val tenantRepository = mockk<TenantRepository>()
    private val eventPublisher = mockk<TenantEventPublisher>(relaxed = true)
    private val service = ManageTenantService(tenantRepository, eventPublisher)

    private val tenantId = TenantId.of("yayasan-manage-01")

    @Test
    fun `createTenant should validate uniqueness, save tenant, publish events, and return result`() {
        val command = CreateTenantCommand(
            tenantId = "yayasan-manage-01",
            name = "Yayasan Pendidikan Al-Azhar",
            legalEntityNumber = "SK-001",
            registrationType = "YAYASAN",
            address = "Jl. Sisingamangaraja",
            contactEmail = "admin@alazhar.org",
            contactPhone = "021-123456"
        )

        every { tenantRepository.existsById(tenantId) } returns false
        every { tenantRepository.save(any()) } answers { firstArg() }

        val created = service.createTenant(command)

        assertThat(created.id).isEqualTo(tenantId)
        assertThat(created.name).isEqualTo("Yayasan Pendidikan Al-Azhar")
        assertThat(created.legalEntityNumber).isEqualTo("SK-001")
        verify(exactly = 1) { tenantRepository.save(any()) }
        verify(exactly = 1) { eventPublisher.publishAll(any()) }
    }

    @Test
    fun `createTenant should throw DuplicateTenantException when tenantId exists`() {
        val command = CreateTenantCommand(
            tenantId = "yayasan-manage-01",
            name = "Yayasan Al-Azhar",
            address = "Jl. Raya 1",
            contactEmail = "admin@alazhar.org",
            contactPhone = "021-123456"
        )
        every { tenantRepository.existsById(tenantId) } returns true

        assertThatThrownBy { service.createTenant(command) }
            .isInstanceOf(DuplicateTenantException::class.java)
            .hasMessageContaining("A tenant with ID 'yayasan-manage-01' already exists.")
    }

    @Test
    fun `getTenant should return tenant when found`() {
        val tenant = Tenant.create(
            tenantId = tenantId,
            name = "Yayasan Al-Azhar",
            address = "Jl. Raya 1",
            contactEmail = "admin@alazhar.org",
            contactPhone = "021-123456"
        )
        every { tenantRepository.findById(tenantId) } returns tenant

        val found = service.getTenant(tenantId)
        assertThat(found).isEqualTo(tenant)
    }

    @Test
    fun `getTenant should throw TenantNotFoundException when not found`() {
        every { tenantRepository.findById(tenantId) } returns null

        assertThatThrownBy { service.getTenant(tenantId) }
            .isInstanceOf(TenantNotFoundException::class.java)
            .hasMessageContaining("Tenant 'yayasan-manage-01' was not found.")
    }

    @Test
    fun `updateTenant should update details, save, publish events, and return result`() {
        val tenant = Tenant.create(
            tenantId = tenantId,
            name = "Yayasan Al-Azhar",
            address = "Jl. Raya 1",
            contactEmail = "admin@alazhar.org",
            contactPhone = "021-123456"
        )
        every { tenantRepository.findById(tenantId) } returns tenant
        every { tenantRepository.save(any()) } answers { firstArg() }

        val command = UpdateTenantCommand(
            name = "Yayasan Al-Azhar Updated",
            address = "Jl. Raya 2",
            contactEmail = "new@alazhar.org",
            contactPhone = "021-999999",
            status = TenantStatus.SUSPENDED
        )

        val updated = service.updateTenant(tenantId, command)

        assertThat(updated.name).isEqualTo("Yayasan Al-Azhar Updated")
        assertThat(updated.status).isEqualTo(TenantStatus.SUSPENDED)
        verify(exactly = 1) { tenantRepository.save(any()) }
        verify(exactly = 1) { eventPublisher.publishAll(any()) }
    }

    @Test
    fun `deleteTenant should delete from repository when tenant exists`() {
        every { tenantRepository.existsById(tenantId) } returns true
        every { tenantRepository.deleteById(tenantId) } returns Unit

        service.deleteTenant(tenantId)

        verify(exactly = 1) { tenantRepository.deleteById(tenantId) }
    }

    @Test
    fun `deleteTenant should throw TenantNotFoundException when tenant does not exist`() {
        every { tenantRepository.existsById(tenantId) } returns false

        assertThatThrownBy { service.deleteTenant(tenantId) }
            .isInstanceOf(TenantNotFoundException::class.java)
    }
}
