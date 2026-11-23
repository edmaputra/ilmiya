package io.github.edmaputra.ilmiya.app.scope

import io.github.edmaputra.iam.domain.model.ScopeNode
import io.github.edmaputra.iam.domain.model.ScopeNodeId
import io.github.edmaputra.iam.domain.repository.ScopeNodeRepository
import io.github.edmaputra.iam.domain.tenancy.TenantId as IamTenantId
import io.github.edmaputra.ilmiya.common.domain.TenantId
import io.github.edmaputra.ilmiya.core.school.domain.SchoolId
import io.github.edmaputra.ilmiya.core.school.domain.SchoolRegisteredEvent
import io.github.edmaputra.ilmiya.core.school.domain.port.out.SchoolRepository
import io.github.edmaputra.ilmiya.core.security.scope.ScopeProvisioningEventListener
import io.github.edmaputra.ilmiya.core.security.scope.ScopeProvisioningService
import io.github.edmaputra.ilmiya.core.tenant.domain.Tenant
import io.github.edmaputra.ilmiya.core.tenant.domain.TenantCreatedEvent
import io.github.edmaputra.ilmiya.core.tenant.domain.port.out.TenantRepository
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.Optional
import java.util.UUID

/**
 * Integration-style flow test verifying that [ScopeProvisioningEventListener] correctly
 * delegates to [ScopeProvisioningService] upon tenant creation and school registration events,
 * and that [ScopeProvisioningService] persists the expected scope tree nodes.
 *
 * Note: [ScopeProvisioningEventListener] uses @TransactionalEventListener(AFTER_COMMIT),
 * so its handlers are called directly here (simulating post-commit dispatch) to verify
 * the end-to-end provisioning logic without requiring a full Spring transaction context.
 */
class ScopeProvisioningFlowTest {

    private val scopeNodeRepository = mockk<ScopeNodeRepository>()
    private val tenantRepository = mockk<TenantRepository>()
    private val schoolRepository = mockk<SchoolRepository>()

    private val scopeProvisioningService = ScopeProvisioningService(scopeNodeRepository, tenantRepository)
    private val listener = ScopeProvisioningEventListener(scopeProvisioningService, tenantRepository)

    private val tenantUuid = UUID.randomUUID()
    private val tenantId = TenantId.of(tenantUuid)
    private val iamTenantId = IamTenantId(tenantUuid)

    private val capturedNodes = mutableListOf<ScopeNode>()

    @BeforeEach
    fun setUp() {
        capturedNodes.clear()
        every { scopeNodeRepository.save(capture(capturedNodes)) } answers { firstArg() }
    }

    @Test
    fun `creating tenant should automatically provision tenant root scope node with deterministic id`() {
        every { scopeNodeRepository.findByTenantIdAndCode(iamTenantId, "ROOT") } returns Optional.empty()

        val event = TenantCreatedEvent(
            tenantId = tenantId,
            name = "Yayasan Global Darussalam",
            contactEmail = "info@darussalam.org"
        )

        // Simulate post-commit dispatch: call handler directly
        listener.onTenantCreated(event)

        val rootNode = capturedNodes.find { it.code == "ROOT" }
        assertThat(rootNode).isNotNull
        assertThat(rootNode!!.id.value()).isEqualTo(tenantUuid) // deterministic ID
        assertThat(rootNode.code).isEqualTo("ROOT")
        assertThat(rootNode.name).isEqualTo("Yayasan Global Darussalam Root Scope")
        assertThat(rootNode.path).isEqualTo("/${tenantId.value}/")
        assertThat(rootNode.parentId).isNull()
        assertThat(rootNode.isRoot).isTrue()

        // Verify depth == 0
        val depth = Math.max(0, rootNode.path.chars().filter { it == '/'.code }.count() - 2)
        assertThat(depth).isEqualTo(0L)
    }

    @Test
    fun `registering school should automatically provision campus anchor scope node under tenant root`() {
        val rootNodeId = ScopeNodeId(tenantUuid) // deterministic root ID
        val existingRootNode = ScopeNode(
            rootNodeId,
            iamTenantId,
            null,
            "ROOT",
            "Yayasan Global Darussalam Root Scope",
            "/${tenantId.value}/",
            java.time.Instant.now(),
            java.time.Instant.now()
        )

        val schoolUuid = UUID.randomUUID()
        val schoolId = SchoolId.of(schoolUuid.toString())

        every { scopeNodeRepository.findByTenantIdAndCode(iamTenantId, "ROOT") } returns Optional.of(existingRootNode)
        every { scopeNodeRepository.findByTenantIdAndCode(iamTenantId, "DAR-CAMPUS-A") } returns Optional.empty()

        val event = SchoolRegisteredEvent(
            schoolId = schoolId,
            tenantId = tenantId,
            institutionCode = "DAR-CAMPUS-A",
            name = "SMA Darussalam Campus A"
        )

        // Simulate post-commit dispatch: call handler directly
        listener.onSchoolRegistered(event)

        val anchorNode = capturedNodes.find { it.code == "DAR-CAMPUS-A" }
        assertThat(anchorNode).isNotNull
        assertThat(anchorNode!!.id.value()).isEqualTo(schoolUuid) // deterministic ID
        assertThat(anchorNode.code).isEqualTo("DAR-CAMPUS-A")
        assertThat(anchorNode.name).isEqualTo("SMA Darussalam Campus A")
        assertThat(anchorNode.path).isEqualTo("/${tenantId.value}/${schoolId.value}/")
        assertThat(anchorNode.parentId).isEqualTo(rootNodeId)
        assertThat(anchorNode.isRoot).isFalse()

        // Verify depth == 1
        val depth = Math.max(0, anchorNode.path.chars().filter { it == '/'.code }.count() - 2)
        assertThat(depth).isEqualTo(1L)
    }

    @Test
    fun `full flow — tenant creation followed by school registration provisions complete scope tree`() {
        every { scopeNodeRepository.findByTenantIdAndCode(iamTenantId, "ROOT") } returns Optional.empty()

        val tenantEvent = TenantCreatedEvent(
            tenantId = tenantId,
            name = "Yayasan Global Darussalam",
            contactEmail = "info@darussalam.org"
        )
        listener.onTenantCreated(tenantEvent)

        val rootNode = capturedNodes.find { it.code == "ROOT" }
        assertThat(rootNode).isNotNull

        val schoolUuid = UUID.randomUUID()
        val schoolId = SchoolId.of(schoolUuid.toString())
        every { scopeNodeRepository.findByTenantIdAndCode(iamTenantId, "ROOT") } returns Optional.of(rootNode!!)
        every { scopeNodeRepository.findByTenantIdAndCode(iamTenantId, "SMK-01") } returns Optional.empty()

        val schoolEvent = SchoolRegisteredEvent(
            schoolId = schoolId,
            tenantId = tenantId,
            institutionCode = "SMK-01",
            name = "SMK Darussalam Satu"
        )
        listener.onSchoolRegistered(schoolEvent)

        val anchorNode = capturedNodes.find { it.code == "SMK-01" }
        assertThat(anchorNode).isNotNull
        assertThat(anchorNode!!.parentId).isEqualTo(rootNode.id)
        assertThat(capturedNodes).hasSize(2)
    }

    @Test
    fun `school profile update should re-sync campus anchor scope without extra db lookup`() {
        val rootNodeId = ScopeNodeId(tenantUuid)
        val existingRootNode = ScopeNode(
            rootNodeId, iamTenantId, null, "ROOT", "Darussalam Root Scope",
            "/${tenantId.value}/", java.time.Instant.now(), java.time.Instant.now()
        )
        val schoolUuid = UUID.randomUUID()
        val schoolId = SchoolId.of(schoolUuid.toString())
        val existingAnchor = ScopeNode(
            ScopeNodeId(schoolUuid), iamTenantId, rootNodeId, "DAR-MAIN",
            "Old School Name", "/${tenantId.value}/${schoolId.value}/",
            java.time.Instant.now(), java.time.Instant.now()
        )

        every { scopeNodeRepository.findByTenantIdAndCode(iamTenantId, "ROOT") } returns Optional.of(existingRootNode)
        every { scopeNodeRepository.findByTenantIdAndCode(iamTenantId, "DAR-MAIN") } returns Optional.of(existingAnchor)

        val event = io.github.edmaputra.ilmiya.core.school.domain.SchoolProfileUpdatedEvent(
            schoolId = schoolId,
            tenantId = tenantId,
            institutionCode = "DAR-MAIN",
            name = "New School Name"
        )

        listener.onSchoolProfileUpdated(event)

        // The name update should have been saved
        assertThat(capturedNodes).hasSize(1)
        assertThat(capturedNodes.first().name).isEqualTo("New School Name")
    }
}
