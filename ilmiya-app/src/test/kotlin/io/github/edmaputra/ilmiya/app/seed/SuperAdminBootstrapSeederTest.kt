package io.github.edmaputra.ilmiya.app.seed

import io.github.edmaputra.iam.application.port.`in`.ManageUserUseCase
import io.github.edmaputra.iam.domain.model.User
import io.github.edmaputra.iam.domain.model.UserId
import io.github.edmaputra.iam.domain.model.UserStatus
import io.github.edmaputra.iam.domain.repository.UserRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.UUID

class SuperAdminBootstrapSeederTest {

    private val userRepository = mockk<UserRepository>()
    private val manageUserUseCase = mockk<ManageUserUseCase>()

    private lateinit var seeder: SuperAdminBootstrapSeeder

    @BeforeEach
    fun setUp() {
        seeder = SuperAdminBootstrapSeeder(
            userRepository = userRepository,
            manageUserUseCase = manageUserUseCase
        )
    }

    @Test
    fun `should create superadmin account when absent`() {
        every { userRepository.existsByEmail(SuperAdminBootstrapSeeder.SUPERADMIN_EMAIL) } returns false

        val sampleSuperAdmin = User(
            UserId.generate(),
            SuperAdminBootstrapSeeder.SUPERADMIN_EMAIL,
            "hashed-pw",
            "Platform Super Admin",
            UserStatus.ACTIVE,
            true,
            Instant.now(),
            Instant.now()
        )
        every { manageUserUseCase.createUser(match { it.email == SuperAdminBootstrapSeeder.SUPERADMIN_EMAIL }) } returns sampleSuperAdmin

        seeder.run(mockk())

        verify(exactly = 1) {
            manageUserUseCase.createUser(match {
                it.email == SuperAdminBootstrapSeeder.SUPERADMIN_EMAIL &&
                    it.password == SuperAdminBootstrapSeeder.SUPERADMIN_PASSWORD &&
                    it.platformSuperAdmin
            })
        }
    }

    @Test
    fun `should skip creating superadmin account when already exists`() {
        every { userRepository.existsByEmail(SuperAdminBootstrapSeeder.SUPERADMIN_EMAIL) } returns true

        seeder.run(mockk())

        verify(exactly = 0) { manageUserUseCase.createUser(any()) }
    }
}
