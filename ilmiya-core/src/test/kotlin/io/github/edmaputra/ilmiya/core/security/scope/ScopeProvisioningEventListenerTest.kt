package io.github.edmaputra.ilmiya.core.security.scope

import io.github.edmaputra.iam.domain.model.ScopeNode
import io.github.edmaputra.iam.domain.model.ScopeNodeId
import io.github.edmaputra.iam.domain.tenancy.TenantId as IamTenantId
import io.github.edmaputra.ilmiya.common.domain.TenantId
import io.github.edmaputra.ilmiya.core.school.domain.SchoolId
import io.github.edmaputra.ilmiya.core.school.domain.SchoolProfileUpdatedEvent
import io.github.edmaputra.ilmiya.core.school.domain.SchoolRegisteredEvent
import io.github.edmaputra.ilmiya.core.tenant.domain.Tenant
import io.github.edmaputra.ilmiya.core.tenant.domain.TenantCreatedEvent
import io.github.edmaputra.ilmiya.core.tenant.domain.TenantStatus
import io.github.edmaputra.ilmiya.core.tenant.domain.TenantStatusChangedEvent
import io.github.edmaputra.ilmiya.core.tenant.domain.TenantUpdatedEvent
import io.github.edmaputra.ilmiya.core.tenant.domain.port.out.TenantRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.UUID

class ScopeProvisioningEventListenerTest {

    private val scopeProvisioningService = mockk<ScopeProvisioningService>()
    private val tenantRepository = mockk<TenantRepository>()

    private val listener = ScopeProvisioningEventListener(
        scopeProvisioningService,
        tenantRepository
    )

    private val tenantUuid = UUID.randomUUID()
    private val tenantId = TenantId.of(tenantUuid)
    private val dummyScopeNode = ScopeNode(
        ScopeNodeId.generate(),
        IamTenantId(tenantUuid),
        null,
        "ROOT",
        "Tenant Root Scope",
        "/$tenantUuid/",
        Instant.now(),
        Instant.now()
    )

    @BeforeEach
    fun setUp() {
        every { scopeProvisioningService.provisionTenantRootScope(any(), any()) } returns dummyScopeNode
        every { scopeProvisioningService.provisionCampusAnchorScope(any(), any(), any(), any()) } returns dummyScopeNode
    }

    @Test
    fun `onTenantCreated should invoke provisionTenantRootScope`() {
        val event = TenantCreatedEvent(
            tenantId = tenantId,
            name = "Yayasan Darussalam",
            contactEmail = "admin@darussalam.org"
        )

        listener.onTenantCreated(event)

        verify(exactly = 1) {
            scopeProvisioningService.provisionTenantRootScope(tenantId, "Yayasan Darussalam")
        }
    }

    @Test
    fun `onTenantUpdated should invoke provisionTenantRootScope when status is ACTIVE`() {
        val event = TenantUpdatedEvent(
            tenantId = tenantId,
            name = "Yayasan Darussalam Updated",
            status = TenantStatus.ACTIVE
        )

        listener.onTenantUpdated(event)

        verify(exactly = 1) {
            scopeProvisioningService.provisionTenantRootScope(tenantId, "Yayasan Darussalam Updated")
        }
    }

    @Test
    fun `onTenantUpdated should not invoke provisionTenantRootScope when status is SUSPENDED`() {
        val event = TenantUpdatedEvent(
            tenantId = tenantId,
            name = "Yayasan Darussalam Suspended",
            status = TenantStatus.SUSPENDED
        )

        listener.onTenantUpdated(event)

        verify(exactly = 0) {
            scopeProvisioningService.provisionTenantRootScope(any(), any())
        }
    }

    @Test
    fun `onTenantStatusChanged should invoke provisionTenantRootScope when status becomes ACTIVE`() {
        val tenant = Tenant.create(
            tenantId = tenantId,
            name = "Yayasan Darussalam",
            address = "Jl. Merdeka 1",
            contactEmail = "admin@darussalam.org",
            contactPhone = "021-123456"
        )
        every { tenantRepository.findById(tenantId) } returns tenant

        val event = TenantStatusChangedEvent(
            tenantId = tenantId,
            previousStatus = TenantStatus.PENDING_ONBOARDING,
            newStatus = TenantStatus.ACTIVE
        )

        listener.onTenantStatusChanged(event)

        verify(exactly = 1) {
            scopeProvisioningService.provisionTenantRootScope(tenantId, "Yayasan Darussalam")
        }
    }

    @Test
    fun `onSchoolRegistered should invoke provisionCampusAnchorScope`() {
        val schoolId = SchoolId.of(UUID.randomUUID().toString())
        val event = SchoolRegisteredEvent(
            schoolId = schoolId,
            tenantId = tenantId,
            institutionCode = "DAR-HIGH",
            name = "Darussalam High School"
        )

        listener.onSchoolRegistered(event)

        verify(exactly = 1) {
            scopeProvisioningService.provisionCampusAnchorScope(
                tenantId = tenantId,
                schoolId = schoolId,
                institutionCode = "DAR-HIGH",
                schoolName = "Darussalam High School"
            )
        }
    }

    @Test
    fun `onSchoolProfileUpdated should invoke provisionCampusAnchorScope using event fields directly`() {
        val schoolId = SchoolId.of(UUID.randomUUID().toString())
        val event = SchoolProfileUpdatedEvent(
            schoolId = schoolId,
            tenantId = tenantId,
            institutionCode = "DAR-HIGH",
            name = "Darussalam High School Updated"
        )

        listener.onSchoolProfileUpdated(event)

        verify(exactly = 1) {
            scopeProvisioningService.provisionCampusAnchorScope(
                tenantId = tenantId,
                schoolId = schoolId,
                institutionCode = "DAR-HIGH",
                schoolName = "Darussalam High School Updated"
            )
        }
    }
}
