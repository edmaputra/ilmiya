package io.github.edmaputra.ilmiya.app.scope

import io.github.edmaputra.iam.domain.model.ScopeNode
import io.github.edmaputra.iam.domain.model.ScopeNodeId
import io.github.edmaputra.iam.domain.repository.ScopeNodeRepository
import io.github.edmaputra.iam.domain.tenancy.TenantId as IamTenantId
import io.github.edmaputra.ilmiya.app.event.SpringSchoolEventPublisher
import io.github.edmaputra.ilmiya.app.event.SpringTenantEventPublisher
import io.github.edmaputra.ilmiya.app.school.TransactionalRegisterSchoolUseCase
import io.github.edmaputra.ilmiya.app.tenant.TransactionalManageTenantUseCase
import io.github.edmaputra.ilmiya.common.domain.TenantId
import io.github.edmaputra.ilmiya.core.school.domain.School
import io.github.edmaputra.ilmiya.core.school.domain.SchoolId
import io.github.edmaputra.ilmiya.core.school.domain.port.`in`.RegisterSchoolCommand
import io.github.edmaputra.ilmiya.core.school.domain.port.out.SchoolRepository
import io.github.edmaputra.ilmiya.core.security.scope.ScopeProvisioningEventListener
import io.github.edmaputra.ilmiya.core.security.scope.ScopeProvisioningService
import io.github.edmaputra.ilmiya.core.tenant.domain.Tenant
import io.github.edmaputra.ilmiya.core.tenant.domain.port.`in`.CreateTenantCommand
import io.github.edmaputra.ilmiya.core.tenant.domain.port.out.TenantProvisioningPort
import io.github.edmaputra.ilmiya.core.tenant.domain.port.out.TenantRepository
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.context.event.DefaultEventListenerFactory
import org.springframework.context.event.EventListenerMethodProcessor
import java.util.Optional
import java.util.UUID

/**
 * End-to-end integration test verifying that tenant activation and school registration
 * automatically provision valid root and campus anchor scope nodes via domain events.
 */
class ScopeProvisioningFlowTest {

    private val scopeNodeRepository = mockk<ScopeNodeRepository>()
    private val tenantRepository = mockk<TenantRepository>()
    private val schoolRepository = mockk<SchoolRepository>()
    private val tenantProvisioningPort = mockk<TenantProvisioningPort>(relaxed = true)

    private lateinit var applicationContext: AnnotationConfigApplicationContext
    private lateinit var scopeProvisioningService: ScopeProvisioningService
    private lateinit var listener: ScopeProvisioningEventListener
    private lateinit var tenantEventPublisher: SpringTenantEventPublisher
    private lateinit var schoolEventPublisher: SpringSchoolEventPublisher
    private lateinit var tenantUseCase: TransactionalManageTenantUseCase
    private lateinit var schoolUseCase: TransactionalRegisterSchoolUseCase

    private val tenantUuid = UUID.randomUUID()
    private val tenantId = TenantId.of(tenantUuid)
    private val iamTenantId = IamTenantId(tenantUuid)

    private val capturedNodes = mutableListOf<ScopeNode>()

    @BeforeEach
    fun setUp() {
        capturedNodes.clear()
        every { scopeNodeRepository.save(capture(capturedNodes)) } answers { firstArg() }

        scopeProvisioningService = ScopeProvisioningService(scopeNodeRepository, tenantRepository)
        listener = ScopeProvisioningEventListener(scopeProvisioningService, tenantRepository, schoolRepository)

        applicationContext = AnnotationConfigApplicationContext()
        applicationContext.beanFactory.registerSingleton("scopeProvisioningEventListener", listener)
        applicationContext.register(EventListenerMethodProcessor::class.java, DefaultEventListenerFactory::class.java)
        applicationContext.refresh()

        tenantEventPublisher = SpringTenantEventPublisher(applicationContext)
        schoolEventPublisher = SpringSchoolEventPublisher(applicationContext)

        tenantUseCase = TransactionalManageTenantUseCase(tenantRepository, tenantEventPublisher, tenantProvisioningPort)
        schoolUseCase = TransactionalRegisterSchoolUseCase(schoolRepository, schoolEventPublisher)
    }

    @AfterEach
    fun tearDown() {
        applicationContext.close()
    }

    @Test
    fun `creating tenant should automatically provision tenant root scope node`() {
        val createCmd = CreateTenantCommand(
            tenantId = tenantUuid.toString(),
            name = "Yayasan Global Darussalam",
            address = "Jl. Sudirman 10",
            contactEmail = "info@darussalam.org",
            contactPhone = "021-5551234"
        )
        val createdTenant = Tenant.create(
            tenantId = tenantId,
            name = createCmd.name,
            address = createCmd.address,
            contactEmail = createCmd.contactEmail,
            contactPhone = createCmd.contactPhone
        )

        every { tenantRepository.existsById(tenantId) } returns false
        every { tenantRepository.save(any()) } returns createdTenant
        every { scopeNodeRepository.findByTenantIdAndCode(iamTenantId, "ROOT") } returns Optional.empty()

        tenantUseCase.createTenant(createCmd)

        val rootNode = capturedNodes.find { it.code == "ROOT" }
        assertThat(rootNode).isNotNull
        assertThat(rootNode!!.code).isEqualTo("ROOT")
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
        val rootNodeId = ScopeNodeId.generate()
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
        val registerCmd = RegisterSchoolCommand(
            tenantId = tenantId.value,
            name = "SMA Darussalam Campus A",
            institutionCode = "DAR-CAMPUS-A",
            address = "Jl. Merdeka 5",
            contactEmail = "admin@campusa.sch.id",
            contactPhone = "021-999888"
        )
        val registeredSchool = School.create(
            id = SchoolId.of(schoolUuid.toString()),
            tenantId = tenantId,
            name = registerCmd.name,
            institutionCode = registerCmd.institutionCode,
            address = registerCmd.address,
            contactEmail = registerCmd.contactEmail,
            contactPhone = registerCmd.contactPhone
        )

        val savedSchoolSlot = io.mockk.slot<School>()
        every { schoolRepository.existsByInstitutionCode("DAR-CAMPUS-A") } returns false
        every { schoolRepository.save(capture(savedSchoolSlot)) } answers { firstArg() }
        every { scopeNodeRepository.findByTenantIdAndCode(iamTenantId, "ROOT") } returns Optional.of(existingRootNode)
        every { scopeNodeRepository.findByTenantIdAndCode(iamTenantId, "DAR-CAMPUS-A") } returns Optional.empty()

        schoolUseCase.register(registerCmd)

        val anchorNode = capturedNodes.find { it.code == "DAR-CAMPUS-A" }
        assertThat(anchorNode).isNotNull
        assertThat(anchorNode!!.code).isEqualTo("DAR-CAMPUS-A")
        assertThat(anchorNode.name).isEqualTo("SMA Darussalam Campus A")
        assertThat(anchorNode.path).isEqualTo("/${tenantId.value}/${savedSchoolSlot.captured.id.value}/")
        assertThat(anchorNode.parentId).isEqualTo(rootNodeId)
        assertThat(anchorNode.isRoot).isFalse()

        // Verify depth == 1
        val depth = Math.max(0, anchorNode.path.chars().filter { it == '/'.code }.count() - 2)
        assertThat(depth).isEqualTo(1L)
    }
}
