package io.github.edmaputra.ilmiya.core.security.scope

import io.github.edmaputra.iam.domain.model.ScopeNode
import io.github.edmaputra.iam.domain.model.ScopeNodeId
import io.github.edmaputra.iam.domain.repository.ScopeNodeRepository
import io.github.edmaputra.iam.domain.tenancy.TenantId as IamTenantId
import io.github.edmaputra.ilmiya.common.domain.TenantId
import io.github.edmaputra.ilmiya.core.school.domain.SchoolId
import io.github.edmaputra.ilmiya.core.tenant.domain.Tenant
import io.github.edmaputra.ilmiya.core.tenant.domain.port.out.TenantRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.Optional
import java.util.UUID

class ScopeProvisioningServiceTest {

    private val scopeNodeRepository = mockk<ScopeNodeRepository>()
    private val tenantRepository = mockk<TenantRepository>()
    private val service = ScopeProvisioningService(scopeNodeRepository, tenantRepository)

    private val tenantUuid = UUID.randomUUID()
    private val tenantId = TenantId.of(tenantUuid)
    private val iamTenantId = IamTenantId(tenantUuid)

    private val schoolUuid = UUID.randomUUID()
    private val schoolId = SchoolId.of(schoolUuid.toString())

    @BeforeEach
    fun setUp() {
        every { scopeNodeRepository.save(any()) } answers { firstArg() }
    }

    @Test
    fun `provisionTenantRootScope should create root scope node with code ROOT, depth 0, and null parent`() {
        every { scopeNodeRepository.findByTenantIdAndCode(iamTenantId, "ROOT") } returns Optional.empty()

        val rootScope = service.provisionTenantRootScope(tenantId, "Darussalam Islamic Foundation")

        assertThat(rootScope.id).isNotNull
        assertThat(rootScope.tenantId).isEqualTo(iamTenantId)
        assertThat(rootScope.parentId).isNull()
        assertThat(rootScope.isRoot).isTrue()
        assertThat(rootScope.code).isEqualTo("ROOT")
        assertThat(rootScope.name).isEqualTo("Darussalam Islamic Foundation Root Scope")
        assertThat(rootScope.path).isEqualTo("/${tenantId.value}/")

        // Depth / level calculation verification
        val depth = Math.max(0, rootScope.path.chars().filter { it == '/'.code }.count() - 2)
        assertThat(depth).isEqualTo(0L)

        verify(exactly = 1) { scopeNodeRepository.save(any()) }
    }

    @Test
    fun `provisionTenantRootScope should be idempotent and return existing root scope`() {
        val existingNode = ScopeNode(
            ScopeNodeId.generate(),
            iamTenantId,
            null,
            "ROOT",
            "Darussalam Foundation Root Scope",
            "/${tenantId.value}/",
            Instant.now(),
            Instant.now()
        )
        every { scopeNodeRepository.findByTenantIdAndCode(iamTenantId, "ROOT") } returns Optional.of(existingNode)

        val result = service.provisionTenantRootScope(tenantId, "Darussalam Foundation")

        assertThat(result.id).isEqualTo(existingNode.id)
        assertThat(result.name).isEqualTo("Darussalam Foundation Root Scope")
        verify(exactly = 0) { scopeNodeRepository.save(any()) }
    }

    @Test
    fun `provisionTenantRootScope should update name if tenant name changed`() {
        val existingNode = ScopeNode(
            ScopeNodeId.generate(),
            iamTenantId,
            null,
            "ROOT",
            "Old Foundation Name Root Scope",
            "/${tenantId.value}/",
            Instant.now(),
            Instant.now()
        )
        every { scopeNodeRepository.findByTenantIdAndCode(iamTenantId, "ROOT") } returns Optional.of(existingNode)

        val result = service.provisionTenantRootScope(tenantId, "New Foundation Name")

        assertThat(result.name).isEqualTo("New Foundation Name Root Scope")
        verify(exactly = 1) { scopeNodeRepository.save(any()) }
    }

    @Test
    fun `provisionCampusAnchorScope should create campus anchor under tenant root with depth 1`() {
        val rootNode = ScopeNode(
            ScopeNodeId.generate(),
            iamTenantId,
            null,
            "ROOT",
            "Darussalam Root Scope",
            "/${tenantId.value}/",
            Instant.now(),
            Instant.now()
        )
        every { scopeNodeRepository.findByTenantIdAndCode(iamTenantId, "ROOT") } returns Optional.of(rootNode)
        every { scopeNodeRepository.findByTenantIdAndCode(iamTenantId, "SCH-DAR-01") } returns Optional.empty()

        val anchorScope = service.provisionCampusAnchorScope(
            tenantId = tenantId,
            schoolId = schoolId,
            institutionCode = "SCH-DAR-01",
            schoolName = "Darussalam Senior High School"
        )

        assertThat(anchorScope.id.value()).isEqualTo(schoolUuid)
        assertThat(anchorScope.tenantId).isEqualTo(iamTenantId)
        assertThat(anchorScope.parentId).isEqualTo(rootNode.id)
        assertThat(anchorScope.isRoot).isFalse()
        assertThat(anchorScope.code).isEqualTo("SCH-DAR-01")
        assertThat(anchorScope.name).isEqualTo("Darussalam Senior High School")
        assertThat(anchorScope.path).isEqualTo("/${tenantId.value}/${schoolId.value}/")

        // Depth / level calculation verification
        val depth = Math.max(0, anchorScope.path.chars().filter { it == '/'.code }.count() - 2)
        assertThat(depth).isEqualTo(1L)

        verify(exactly = 1) { scopeNodeRepository.save(any()) }
    }

    @Test
    fun `provisionCampusAnchorScope should auto-provision tenant root scope if missing`() {
        val sampleTenant = Tenant.create(
            tenantId = tenantId,
            name = "Yayasan Darussalam",
            address = "Jl. Merdeka 1",
            contactEmail = "admin@darussalam.org",
            contactPhone = "021-123456"
        )
        every { scopeNodeRepository.findByTenantIdAndCode(iamTenantId, "ROOT") } returns Optional.empty()
        every { tenantRepository.findById(tenantId) } returns sampleTenant
        every { scopeNodeRepository.findByTenantIdAndCode(iamTenantId, "SCH-01") } returns Optional.empty()

        val anchorScope = service.provisionCampusAnchorScope(
            tenantId = tenantId,
            schoolId = schoolId,
            institutionCode = "sch-01",
            schoolName = "SMA Darussalam"
        )

        assertThat(anchorScope.code).isEqualTo("SCH-01")
        assertThat(anchorScope.parentId).isNotNull
        assertThat(anchorScope.path).isEqualTo("/${tenantId.value}/${schoolId.value}/")

        // Both root and campus anchor scope were saved
        verify(exactly = 2) { scopeNodeRepository.save(any()) }
    }

    @Test
    fun `provisionCampusAnchorScope should be idempotent and return existing campus anchor`() {
        val rootNode = ScopeNode(
            ScopeNodeId.generate(),
            iamTenantId,
            null,
            "ROOT",
            "Darussalam Root Scope",
            "/${tenantId.value}/",
            Instant.now(),
            Instant.now()
        )
        val existingAnchor = ScopeNode(
            ScopeNodeId(schoolUuid),
            iamTenantId,
            rootNode.id,
            "SCH-DAR-01",
            "Darussalam Senior High School",
            "/${tenantId.value}/${schoolId.value}/",
            Instant.now(),
            Instant.now()
        )
        every { scopeNodeRepository.findByTenantIdAndCode(iamTenantId, "ROOT") } returns Optional.of(rootNode)
        every { scopeNodeRepository.findByTenantIdAndCode(iamTenantId, "SCH-DAR-01") } returns Optional.of(existingAnchor)

        val result = service.provisionCampusAnchorScope(
            tenantId = tenantId,
            schoolId = schoolId,
            institutionCode = "SCH-DAR-01",
            schoolName = "Darussalam Senior High School"
        )

        assertThat(result.id).isEqualTo(existingAnchor.id)
        verify(exactly = 0) { scopeNodeRepository.save(any()) }
    }
}
