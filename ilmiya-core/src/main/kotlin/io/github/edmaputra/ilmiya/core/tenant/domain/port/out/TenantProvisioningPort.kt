package io.github.edmaputra.ilmiya.core.tenant.domain.port.out

import io.github.edmaputra.ilmiya.common.domain.TenantId

/**
 * Command carrying parameters required to provision a foundation tenant's auxiliary systems
 * and setup its initial tenant administrator user account.
 *
 * @property tenantId target foundation tenant identifier
 * @property name foundation display name
 * @property contactEmail primary administrative contact email
 * @property adminEmail administrator user email address
 * @property password plain text initial password
 * @property fullName display name of the administrator
 * @author edmaputra
 * @since 0.1.0
 */
data class ProvisionTenantCommand(
    val tenantId: TenantId,
    val name: String,
    val contactEmail: String,
    val adminEmail: String,
    val password: String,
    val fullName: String
)

/**
 * Summary result of a tenant provisioning operation.
 *
 * @property adminUserId unique identifier of the administrator user account in the IAM subsystem
 * @property adminEmail administrator user email address
 * @property adminFullName administrator user display name
 * @author edmaputra
 * @since 0.1.0
 */
data class TenantProvisioningResult(
    val adminUserId: String,
    val adminEmail: String,
    val adminFullName: String
)

/**
 * Outbound port contract for provisioning tenant boundary resources and user accounts.
 *
 * @author edmaputra
 * @since 0.1.0
 */
interface TenantProvisioningPort {

    /**
     * Provisions auxiliary tenant resources, sets up the tenant administrator account,
     * and assigns the TENANT_ADMIN role scoped tenant-wide for the given foundation tenant.
     *
     * @param command provisioning instructions
     * @return summary result of the tenant provisioning operation
     */
    fun provisionTenant(command: ProvisionTenantCommand): TenantProvisioningResult
}
