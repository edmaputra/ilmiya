package io.github.edmaputra.ilmiya.core.security.scope

import io.github.edmaputra.iam.domain.model.ScopeNode
import io.github.edmaputra.iam.domain.model.ScopeNodeId
import io.github.edmaputra.iam.domain.repository.ScopeNodeRepository
import io.github.edmaputra.iam.domain.tenancy.TenantId as IamTenantId
import io.github.edmaputra.ilmiya.common.domain.TenantId
import io.github.edmaputra.ilmiya.core.school.domain.SchoolId
import io.github.edmaputra.ilmiya.core.tenant.domain.port.out.TenantRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.UUID

/**
 * Service orchestrating the auto-provisioning of hierarchical IAM scope tree nodes
 * upon tenant and school / campus activation.
 *
 * Guarantees valid foreign key references for tenant- and school-scoped role assignments
 * and eliminates manual anchor setup.
 *
 * @author edmaputra
 * @since 0.1.0
 */
@Service
class ScopeProvisioningService(
    private val scopeNodeRepository: ScopeNodeRepository,
    private val tenantRepository: TenantRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * Provisions the Tenant Root Scope in the scopes table if not already present.
     *
     * Specification:
     * - path: /<tenant_id>/
     * - code: ROOT
     * - name: <tenant_name> Root Scope
     * - depth: 0
     * - parent_id: null
     *
     * @param tenantId unique identifier of the tenant foundation
     * @param tenantName name of the tenant foundation
     * @return the existing or newly created root [ScopeNode]
     */
    @Transactional
    fun provisionTenantRootScope(tenantId: TenantId, tenantName: String): ScopeNode {
        val iamTenantId = IamTenantId(tenantId.toUuid())
        val existing = scopeNodeRepository.findByTenantIdAndCode(iamTenantId, ROOT_SCOPE_CODE)
        if (existing.isPresent) {
            val node = existing.get()
            val formattedName = formatRootScopeName(tenantName)
            if (node.name != formattedName) {
                node.updateMetadata(ROOT_SCOPE_CODE, formattedName)
                log.info("Updated existing tenant root scope name: tenantId={}, name={}", tenantId.value, formattedName)
                return scopeNodeRepository.save(node)
            }
            return node
        }

        val rootScopeId = ScopeNodeId.generate()
        val path = "/${tenantId.value}/"
        val now = Instant.now()
        val rootNode = ScopeNode(
            rootScopeId,
            iamTenantId,
            null,
            ROOT_SCOPE_CODE,
            formatRootScopeName(tenantName),
            path,
            now,
            now
        )
        val saved = scopeNodeRepository.save(rootNode)
        log.info(
            "Auto-provisioned tenant root scope: id={}, tenantId={}, code={}, path={}",
            saved.id.value(),
            tenantId.value,
            ROOT_SCOPE_CODE,
            path
        )
        return saved
    }

    /**
     * Provisions a Campus Anchor Scope under the tenant root in the scopes table if not already present.
     * Automatically provisions the Tenant Root Scope first if it does not yet exist.
     *
     * Specification:
     * - path: /<tenant_id>/<school_id>/
     * - code: <school_institution_code>
     * - name: <school_name>
     * - depth: 1
     * - parent_id: <tenant_root_scope_id>
     *
     * @param tenantId multi-tenant boundary discriminator
     * @param schoolId identifier of the school / campus institution
     * @param institutionCode unique institutional code across the platform
     * @param schoolName official school institution name
     * @return the existing or newly created campus anchor [ScopeNode]
     */
    @Transactional
    fun provisionCampusAnchorScope(
        tenantId: TenantId,
        schoolId: SchoolId,
        institutionCode: String,
        schoolName: String
    ): ScopeNode {
        val iamTenantId = IamTenantId(tenantId.toUuid())
        val normalizedCode = institutionCode.trim().uppercase()

        // 1. Ensure Tenant Root Scope exists
        val rootScope = scopeNodeRepository.findByTenantIdAndCode(iamTenantId, ROOT_SCOPE_CODE)
            .orElseGet {
                val tenant = tenantRepository.findById(tenantId)
                val tenantName = tenant?.name ?: "Tenant"
                provisionTenantRootScope(tenantId, tenantName)
            }

        // 2. Check if campus anchor scope already exists
        val existing = scopeNodeRepository.findByTenantIdAndCode(iamTenantId, normalizedCode)
        if (existing.isPresent) {
            val node = existing.get()
            if (node.name != schoolName.trim()) {
                node.updateMetadata(normalizedCode, schoolName.trim())
                log.info("Updated existing campus anchor scope name: tenantId={}, code={}, name={}", tenantId.value, normalizedCode, schoolName)
                return scopeNodeRepository.save(node)
            }
            return node
        }

        // 3. Create Campus Anchor Scope under tenant root
        val scopeNodeId = runCatching { ScopeNodeId(UUID.fromString(schoolId.value)) }
            .getOrElse { ScopeNodeId.generate() }
        val path = "/${tenantId.value}/${schoolId.value}/"
        val now = Instant.now()
        val anchorNode = ScopeNode(
            scopeNodeId,
            iamTenantId,
            rootScope.id,
            normalizedCode,
            schoolName.trim(),
            path,
            now,
            now
        )
        val saved = scopeNodeRepository.save(anchorNode)
        log.info(
            "Auto-provisioned campus anchor scope: id={}, tenantId={}, schoolId={}, code={}, parentId={}, path={}",
            saved.id.value(),
            tenantId.value,
            schoolId.value,
            normalizedCode,
            rootScope.id.value(),
            path
        )
        return saved
    }

    private fun formatRootScopeName(tenantName: String): String {
        val trimmed = tenantName.trim()
        return if (trimmed.endsWith("Root Scope", ignoreCase = true)) {
            trimmed
        } else {
            "$trimmed Root Scope"
        }
    }

    companion object {
        const val ROOT_SCOPE_CODE = "ROOT"
    }
}
