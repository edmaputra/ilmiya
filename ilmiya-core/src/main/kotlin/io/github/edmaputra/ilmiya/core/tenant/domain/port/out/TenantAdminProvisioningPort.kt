package io.github.edmaputra.ilmiya.core.tenant.domain.port.out

import io.github.edmaputra.ilmiya.common.domain.TenantId

/**
 * Command carrying parameters required to set up a tenant administrator user account.
 *
 * @property tenantId target foundation tenant identifier
 * @property email administrator user email address
 * @property password plain text initial password
 * @property fullName display name of the administrator
 * @author edmaputra
 * @since 0.1.0
 */
data class ProvisionTenantAdminCommand(
    val tenantId: TenantId,
    val email: String,
    val password: String,
    val fullName: String
)

/**
 * Summary record of a provisioned tenant administrator user account.
 *
 * @property userId unique identifier of the user account in the IAM subsystem
 * @property email user email address
 * @property fullName user display name
 * @author edmaputra
 * @since 0.1.0
 */
data class TenantAdminAccount(
    val userId: String,
    val email: String,
    val fullName: String
)

/**
 * Outbound port contract for setting up and provisioning tenant administrator user accounts.
 *
 * @author edmaputra
 * @since 0.1.0
 */
interface TenantAdminProvisioningPort {

    /**
     * Provisions a new or existing user account with the tenant administrator role
     * scoped tenant-wide for the given foundation tenant.
     *
     * @param command provisioning instructions
     * @return account summary of the provisioned tenant admin
     */
    fun provisionTenantAdmin(command: ProvisionTenantAdminCommand): TenantAdminAccount
}
