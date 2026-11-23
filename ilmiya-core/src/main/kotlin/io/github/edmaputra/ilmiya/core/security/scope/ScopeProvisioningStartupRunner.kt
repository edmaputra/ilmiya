package io.github.edmaputra.ilmiya.core.security.scope

import io.github.edmaputra.ilmiya.core.school.domain.SchoolStatus
import io.github.edmaputra.ilmiya.core.school.domain.port.out.SchoolRepository
import io.github.edmaputra.ilmiya.core.tenant.domain.TenantStatus
import io.github.edmaputra.ilmiya.core.tenant.domain.port.out.TenantRepository
import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

/**
 * Startup runner ensuring that all active tenants have root scope nodes
 * and all active schools have campus anchor scope nodes provisioned in the scopes table.
 *
 * Runs automatically upon application startup unless explicitly disabled via
 * `ilmiya.iam.scope.auto-provision.startup-backfill.enabled=false`.
 *
 * @author edmaputra
 * @since 0.1.0
 */
@Component
@Order(Ordered.LOWEST_PRECEDENCE - 50)
@ConditionalOnProperty(
    name = ["ilmiya.iam.scope.auto-provision.startup-backfill.enabled"],
    havingValue = "true",
    matchIfMissing = true
)
class ScopeProvisioningStartupRunner(
    private val scopeProvisioningService: ScopeProvisioningService,
    private val tenantRepository: TenantRepository,
    private val schoolRepository: SchoolRepository
) : ApplicationRunner {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun run(args: ApplicationArguments) {
        backfillScopes()
    }

    /**
     * Checks all existing active tenants and schools, ensuring their root and anchor scope nodes exist.
     *
     * @return pair of (tenantsProvisioned, schoolsProvisioned)
     */
    fun backfillScopes(): Pair<Int, Int> {
        log.info("Checking scope node auto-provisioning for existing active tenants and schools...")
        var tenantCount = 0
        var schoolCount = 0

        // 1. Check all active tenants
        var tenantPage = 0
        val pageSize = 100
        do {
            val tenants = tenantRepository.findAll(tenantPage, pageSize)
            for (tenant in tenants) {
                if (tenant.status == TenantStatus.ACTIVE) {
                    scopeProvisioningService.provisionTenantRootScope(tenant.id, tenant.name)
                    tenantCount++
                }
            }
            tenantPage++
        } while (tenants.size == pageSize)

        // 2. Check all active schools
        var schoolPage = 0
        do {
            val schools = schoolRepository.findAll(schoolPage, pageSize)
            for (school in schools) {
                if (school.status == SchoolStatus.ACTIVE) {
                    scopeProvisioningService.provisionCampusAnchorScope(
                        tenantId = school.tenantId,
                        schoolId = school.id,
                        institutionCode = school.institutionCode,
                        schoolName = school.name
                    )
                    schoolCount++
                }
            }
            schoolPage++
        } while (schools.size == pageSize)

        log.info(
            "Completed scope node auto-provisioning check: verified {} active tenants and {} active schools.",
            tenantCount,
            schoolCount
        )
        return Pair(tenantCount, schoolCount)
    }
}
