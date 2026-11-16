package io.github.edmaputra.ilmiya.core.tenant.adapter.iam

import io.github.edmaputra.iam.application.port.`in`.ManageUserUseCase
import io.github.edmaputra.iam.application.port.`in`.UserCommands
import io.github.edmaputra.iam.domain.model.Role
import io.github.edmaputra.iam.domain.model.RoleId
import io.github.edmaputra.iam.domain.model.User
import io.github.edmaputra.iam.domain.model.UserId
import io.github.edmaputra.iam.domain.model.UserRoleAssignment
import io.github.edmaputra.iam.domain.model.UserStatus
import io.github.edmaputra.iam.domain.repository.RoleRepository
import io.github.edmaputra.iam.domain.repository.UserRepository
import io.github.edmaputra.iam.domain.tenancy.TenantId as IamTenantId
import io.github.edmaputra.ilmiya.common.domain.TenantId
import io.github.edmaputra.ilmiya.core.security.IlmiyaRoles
import io.github.edmaputra.ilmiya.core.tenant.domain.port.out.ProvisionTenantCommand
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.Optional
import java.util.UUID

class TenantProvisioningAdapterTest {

    private val manageUserUseCase = mockk<ManageUserUseCase>()
    private val userRepository = mockk<UserRepository>()
    private val roleRepository = mockk<RoleRepository>()

    private lateinit var adapter: TenantProvisioningAdapter

    private val tenantUuid = UUID.randomUUID()
    private val tenantId = TenantId.of(tenantUuid)
    private val iamTenantId = IamTenantId.from(tenantUuid.toString())

    private val roleId = RoleId.generate()
    private val sampleRole = Role(
        roleId,
        null,
        IlmiyaRoles.TENANT_ADMIN,
        "Tenant Administrator",
        "Tenant Admin Desc",
        true,
        TenantProvisioningAdapter.TENANT_ADMIN_PERMISSIONS,
        Instant.now(),
        Instant.now()
    )

    private val userId = UserId.generate()
    private val sampleUser = User(
        userId,
        "admin@foundation.org",
        "hashed-pw",
        "Foundation Admin",
        UserStatus.ACTIVE,
        false,
        Instant.now(),
        Instant.now()
    )

    @BeforeEach
    fun setUp() {
        adapter = TenantProvisioningAdapter(
            manageUserUseCase = manageUserUseCase,
            userRepository = userRepository,
            roleRepository = roleRepository
        )
    }

    @Test
    fun `provisionTenant should create role and user when absent, then assign role tenant-wide`() {
        val command = ProvisionTenantCommand(
            tenantId = tenantId,
            name = "Foundation Al-Azhar",
            contactEmail = "contact@foundation.org",
            adminEmail = "admin@foundation.org",
            password = "Password123!",
            fullName = "Foundation Admin"
        )

        every { roleRepository.findSystemRoleByCode(IlmiyaRoles.TENANT_ADMIN) } returns Optional.empty()
        every { roleRepository.save(any()) } returns sampleRole

        every { userRepository.findByEmail("admin@foundation.org") } returns Optional.empty()
        every { manageUserUseCase.createUser(any()) } returns sampleUser

        every { manageUserUseCase.getRoleAssignments(userId) } returns emptyList()
        val sampleAssignment = UserRoleAssignment.createTenantWide(userId, roleId, iamTenantId)
        every { manageUserUseCase.assignRole(any()) } returns sampleAssignment

        val result = adapter.provisionTenant(command)

        assertThat(result.adminUserId).isEqualTo(userId.value().toString())
        assertThat(result.adminEmail).isEqualTo("admin@foundation.org")
        assertThat(result.adminFullName).isEqualTo("Foundation Admin")

        verify(exactly = 1) {
            roleRepository.save(match {
                it.code == IlmiyaRoles.TENANT_ADMIN && it.isSystemRole
            })
        }
        verify(exactly = 1) {
            manageUserUseCase.createUser(match {
                it.email == "admin@foundation.org" &&
                    it.password == "Password123!" &&
                    it.fullName == "Foundation Admin" &&
                    !it.platformSuperAdmin
            })
        }
        verify(exactly = 1) {
            manageUserUseCase.assignRole(match {
                it.userId == userId &&
                    it.roleId == roleId &&
                    it.tenantId == iamTenantId &&
                    it.scopeNodeId == null
            })
        }
    }

    @Test
    fun `provisionTenant should reuse existing role and user, and assign role if not yet assigned`() {
        val command = ProvisionTenantCommand(
            tenantId = tenantId,
            name = "Foundation Al-Azhar",
            contactEmail = "contact@foundation.org",
            adminEmail = "admin@foundation.org",
            password = "Password123!",
            fullName = "Foundation Admin"
        )

        every { roleRepository.findSystemRoleByCode(IlmiyaRoles.TENANT_ADMIN) } returns Optional.of(sampleRole)
        every { userRepository.findByEmail("admin@foundation.org") } returns Optional.of(sampleUser)
        every { manageUserUseCase.getRoleAssignments(userId) } returns emptyList()

        val sampleAssignment = UserRoleAssignment.createTenantWide(userId, roleId, iamTenantId)
        every { manageUserUseCase.assignRole(any()) } returns sampleAssignment

        val result = adapter.provisionTenant(command)

        assertThat(result.adminUserId).isEqualTo(userId.value().toString())
        verify(exactly = 0) { roleRepository.save(any()) }
        verify(exactly = 0) { manageUserUseCase.createUser(any()) }
        verify(exactly = 1) {
            manageUserUseCase.assignRole(match {
                it.userId == userId &&
                    it.roleId == roleId &&
                    it.tenantId == iamTenantId
            })
        }
    }

    @Test
    fun `provisionTenant should skip assignRole if user already has TENANT_ADMIN role for tenant`() {
        val command = ProvisionTenantCommand(
            tenantId = tenantId,
            name = "Foundation Al-Azhar",
            contactEmail = "contact@foundation.org",
            adminEmail = "admin@foundation.org",
            password = "Password123!",
            fullName = "Foundation Admin"
        )

        val existingAssignment = UserRoleAssignment.createTenantWide(userId, roleId, iamTenantId)

        every { roleRepository.findSystemRoleByCode(IlmiyaRoles.TENANT_ADMIN) } returns Optional.of(sampleRole)
        every { userRepository.findByEmail("admin@foundation.org") } returns Optional.of(sampleUser)
        every { manageUserUseCase.getRoleAssignments(userId) } returns listOf(existingAssignment)

        val result = adapter.provisionTenant(command)

        assertThat(result.adminUserId).isEqualTo(userId.value().toString())
        verify(exactly = 0) { manageUserUseCase.assignRole(any()) }
    }
}
