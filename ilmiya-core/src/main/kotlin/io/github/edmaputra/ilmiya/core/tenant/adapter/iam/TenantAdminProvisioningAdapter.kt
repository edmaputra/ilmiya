package io.github.edmaputra.ilmiya.core.tenant.adapter.iam

import io.github.edmaputra.iam.application.port.`in`.ManageUserUseCase
import io.github.edmaputra.iam.application.port.`in`.UserCommands
import io.github.edmaputra.iam.domain.model.Role
import io.github.edmaputra.iam.domain.repository.RoleRepository
import io.github.edmaputra.iam.domain.repository.UserRepository
import io.github.edmaputra.iam.domain.tenancy.TenantId as IamTenantId
import io.github.edmaputra.ilmiya.core.security.IlmiyaRoles
import io.github.edmaputra.ilmiya.core.tenant.domain.port.out.ProvisionTenantAdminCommand
import io.github.edmaputra.ilmiya.core.tenant.domain.port.out.TenantAdminAccount
import io.github.edmaputra.ilmiya.core.tenant.domain.port.out.TenantAdminProvisioningPort
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * Secondary driven adapter implementing [TenantAdminProvisioningPort] using ed-iam identity and access services.
 *
 * Ensures the system role `TENANT_ADMIN` exists, provisions or resolves the administrator user account,
 * and establishes a tenant-wide role assignment for the newly provisioned foundation.
 *
 * @author edmaputra
 * @since 0.1.0
 */
@Component
class TenantAdminProvisioningAdapter(
    private val manageUserUseCase: ManageUserUseCase,
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository
) : TenantAdminProvisioningPort {

    private val log = LoggerFactory.getLogger(javaClass)

    companion object {
        val TENANT_ADMIN_PERMISSIONS: Set<String> = setOf(
            "tenant:management:read",
            "tenant:profile:*",
            "tenant:school:*",
            "tenant:subscription:read",
            "tenant:audit:read",
            "school:*",
            "academic:*",
            "student:*",
            "attendance:*",
            "timetable:*",
            "exam:*",
            "finance:*",
            "hr:*",
            "audit:log:*"
        )
    }

    override fun provisionTenantAdmin(command: ProvisionTenantAdminCommand): TenantAdminAccount {
        log.info(
            "Provisioning tenant admin user account for tenant '{}' with email '{}'",
            command.tenantId.value,
            command.email
        )

        // 1. Ensure the TENANT_ADMIN system role exists
        val tenantAdminRole = roleRepository.findSystemRoleByCode(IlmiyaRoles.TENANT_ADMIN)
            .orElseGet {
                log.info("System role '{}' not found in repository. Creating it now.", IlmiyaRoles.TENANT_ADMIN)
                roleRepository.save(
                    Role.createSystemRole(
                        IlmiyaRoles.TENANT_ADMIN,
                        "Tenant Administrator",
                        "Administrator for foundation/tenant organization",
                        TENANT_ADMIN_PERMISSIONS
                    )
                )
            }

        // 2. Resolve or create user account in IAM
        val user = userRepository.findByEmail(command.email)
            .orElseGet {
                log.info("User account '{}' not found in IAM. Creating new user account.", command.email)
                manageUserUseCase.createUser(
                    UserCommands.CreateUserCommand(
                        command.email,
                        command.password,
                        command.fullName,
                        false
                    )
                )
            }

        // 3. Assign TENANT_ADMIN role tenant-wide (scopeNodeId = null)
        val iamTenantId = IamTenantId.from(command.tenantId.value)
        val existingAssignments = manageUserUseCase.getRoleAssignments(user.id)
        val alreadyAssigned = existingAssignments.any { assignment ->
            assignment.roleId == tenantAdminRole.id && assignment.tenantId == iamTenantId
        }

        if (!alreadyAssigned) {
            log.info(
                "Assigning role '{}' to user '{}' for tenant '{}'",
                IlmiyaRoles.TENANT_ADMIN,
                user.id.value(),
                command.tenantId.value
            )
            manageUserUseCase.assignRole(
                UserCommands.AssignUserRoleCommand(
                    user.id,
                    tenantAdminRole.id,
                    iamTenantId,
                    null
                )
            )
        } else {
            log.info(
                "User '{}' already has role '{}' for tenant '{}'",
                user.id.value(),
                IlmiyaRoles.TENANT_ADMIN,
                command.tenantId.value
            )
        }

        return TenantAdminAccount(
            userId = user.id.value().toString(),
            email = user.email,
            fullName = user.fullName
        )
    }
}
