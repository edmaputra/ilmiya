package io.github.edmaputra.ilmiya.core.security.scope

import io.github.edmaputra.iam.domain.model.ScopeNode
import io.github.edmaputra.iam.domain.model.ScopeNodeId
import io.github.edmaputra.iam.domain.tenancy.TenantId as IamTenantId
import io.github.edmaputra.ilmiya.common.domain.TenantId
import io.github.edmaputra.ilmiya.core.school.domain.School
import io.github.edmaputra.ilmiya.core.school.domain.SchoolId
import io.github.edmaputra.ilmiya.core.school.domain.SchoolStatus
import io.github.edmaputra.ilmiya.core.school.domain.port.out.SchoolRepository
import io.github.edmaputra.ilmiya.core.tenant.domain.Tenant
import io.github.edmaputra.ilmiya.core.tenant.domain.TenantStatus
import io.github.edmaputra.ilmiya.core.tenant.domain.port.out.TenantRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.DefaultApplicationArguments
import java.time.Instant
import java.util.UUID

class ScopeProvisioningStartupRunnerTest {

    private val scopeProvisioningService = mockk<ScopeProvisioningService>()
    private val tenantRepository = mockk<TenantRepository>()
    private val schoolRepository = mockk<SchoolRepository>()

    private val runner = ScopeProvisioningStartupRunner(
        scopeProvisioningService,
        tenantRepository,
        schoolRepository
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

    @Test
    fun `run and backfillScopes should verify and provision active tenants and schools`() {
        val tenant1 = Tenant.create(
            tenantId = tenantId,
            name = "Yayasan Satu",
            address = "Jl. 1",
            contactEmail = "a@1.org",
            contactPhone = "021-1",
            status = TenantStatus.ACTIVE
        )
        val tenant2 = Tenant.create(
            tenantId = TenantId.generate(),
            name = "Yayasan Dua (Suspended)",
            address = "Jl. 2",
            contactEmail = "a@2.org",
            contactPhone = "021-2",
            status = TenantStatus.SUSPENDED
        )

        val schoolId = SchoolId.of(UUID.randomUUID().toString())
        val school1 = School.create(
            id = schoolId,
            tenantId = tenantId,
            name = "School Satu",
            institutionCode = "SCH-01",
            address = "Jl. 1",
            contactEmail = "s@1.sch.id",
            contactPhone = "021-1"
        )

        every { tenantRepository.findAll(0, 100) } returns listOf(tenant1, tenant2)
        every { schoolRepository.findAll(0, 100) } returns listOf(school1)
        every { scopeProvisioningService.provisionTenantRootScope(any(), any()) } returns dummyScopeNode
        every { scopeProvisioningService.provisionCampusAnchorScope(any(), any(), any(), any()) } returns dummyScopeNode

        val (tenantsCount, schoolsCount) = runner.backfillScopes()

        assertThat(tenantsCount).isEqualTo(1)
        assertThat(schoolsCount).isEqualTo(1)

        verify(exactly = 1) {
            scopeProvisioningService.provisionTenantRootScope(tenant1.id, tenant1.name)
        }
        verify(exactly = 0) {
            scopeProvisioningService.provisionTenantRootScope(tenant2.id, any())
        }
        verify(exactly = 1) {
            scopeProvisioningService.provisionCampusAnchorScope(
                tenantId = tenant1.id,
                schoolId = school1.id,
                institutionCode = school1.institutionCode,
                schoolName = school1.name
            )
        }

        // Test run invocation
        runner.run(DefaultApplicationArguments())
    }
}
