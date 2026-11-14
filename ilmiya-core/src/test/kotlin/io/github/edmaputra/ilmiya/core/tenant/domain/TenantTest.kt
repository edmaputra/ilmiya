package io.github.edmaputra.ilmiya.core.tenant.domain

import io.github.edmaputra.ilmiya.common.domain.TenantId
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class TenantTest {

    private val tenantId = TenantId.of("yayasan-01")

    @Test
    fun `create should initialize tenant and register TenantCreatedEvent`() {
        val tenant = Tenant.create(
            tenantId = tenantId,
            name = "Yayasan Pendidikan Al-Ikhlas",
            address = "Jl. Raya Kebayoran No. 10",
            contactEmail = "sekretariat@alikhlas.org",
            contactPhone = "+62215550100",
            legalEntityNumber = "AHU-0012345",
            registrationType = "YAYASAN"
        )

        assertThat(tenant.id).isEqualTo(tenantId)
        assertThat(tenant.name).isEqualTo("Yayasan Pendidikan Al-Ikhlas")
        assertThat(tenant.legalEntityNumber).isEqualTo("AHU-0012345")
        assertThat(tenant.status).isEqualTo(TenantStatus.ACTIVE)

        val events = tenant.pullDomainEvents()
        assertThat(events).hasSize(1)
        assertThat(events[0]).isInstanceOf(TenantCreatedEvent::class.java)
        val event = events[0] as TenantCreatedEvent
        assertThat(event.tenantId).isEqualTo(tenantId)
        assertThat(event.name).isEqualTo("Yayasan Pendidikan Al-Ikhlas")
    }

    @Test
    fun `create should reject blank attributes`() {
        assertThatThrownBy {
            Tenant.create(
                tenantId = tenantId,
                name = "",
                address = "Jl. Raya",
                contactEmail = "admin@org.com",
                contactPhone = "123"
            )
        }.isInstanceOf(IllegalArgumentException::class.java)

        assertThatThrownBy {
            Tenant.create(
                tenantId = tenantId,
                name = "Name",
                address = "",
                contactEmail = "admin@org.com",
                contactPhone = "123"
            )
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun `updateProfile should update details and register TenantUpdatedEvent`() {
        val tenant = Tenant.create(
            tenantId = tenantId,
            name = "Yayasan Al-Ikhlas",
            address = "Jl. Raya 1",
            contactEmail = "admin@alikhlas.org",
            contactPhone = "123"
        )
        tenant.pullDomainEvents()

        val updated = tenant.updateProfile(
            name = "Yayasan Al-Ikhlas Baru",
            address = "Jl. Raya 2",
            contactEmail = "admin2@alikhlas.org",
            contactPhone = "456",
            status = TenantStatus.SUSPENDED
        )

        assertThat(updated.name).isEqualTo("Yayasan Al-Ikhlas Baru")
        assertThat(updated.status).isEqualTo(TenantStatus.SUSPENDED)
        val events = updated.pullDomainEvents()
        assertThat(events).hasSize(1)
        assertThat(events[0]).isInstanceOf(TenantUpdatedEvent::class.java)
    }

    @Test
    fun `changeStatus should register TenantStatusChangedEvent when status differs`() {
        val tenant = Tenant.create(
            tenantId = tenantId,
            name = "Yayasan Al-Ikhlas",
            address = "Jl. Raya 1",
            contactEmail = "admin@alikhlas.org",
            contactPhone = "123"
        )
        tenant.pullDomainEvents()

        val suspended = tenant.changeStatus(TenantStatus.SUSPENDED)
        assertThat(suspended.status).isEqualTo(TenantStatus.SUSPENDED)
        val events = suspended.pullDomainEvents()
        assertThat(events).hasSize(1)
        assertThat(events[0]).isInstanceOf(TenantStatusChangedEvent::class.java)

        // No event when same status
        val same = suspended.changeStatus(TenantStatus.SUSPENDED)
        assertThat(same.pullDomainEvents()).isEmpty()
    }

    @Test
    fun `auditableFields should return core tenant attributes`() {
        val tenant = Tenant.create(
            tenantId = tenantId,
            name = "Yayasan Al-Ikhlas",
            address = "Jl. Raya 1",
            contactEmail = "admin@alikhlas.org",
            contactPhone = "123",
            legalEntityNumber = "LEGAL-123"
        )

        val fields = tenant.auditableFields()
        assertThat(fields["id"]).isEqualTo(tenantId.value)
        assertThat(fields["name"]).isEqualTo("Yayasan Al-Ikhlas")
        assertThat(fields["legalEntityNumber"]).isEqualTo("LEGAL-123")
        assertThat(fields["status"]).isEqualTo("ACTIVE")
    }
}
