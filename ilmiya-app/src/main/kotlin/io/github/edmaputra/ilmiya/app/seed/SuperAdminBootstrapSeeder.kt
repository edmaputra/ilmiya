package io.github.edmaputra.ilmiya.app.seed

import io.github.edmaputra.iam.application.port.`in`.ManageUserUseCase
import io.github.edmaputra.iam.application.port.`in`.UserCommands
import io.github.edmaputra.iam.domain.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

/**
 * Bootstrap runner that ensures at least one platform super-administrator exists.
 * Enables initial bootstrapping of tenants, schools, and configurations from scratch.
 *
 * Runs on startup unless disabled via 'ilmiya.bootstrap.superadmin.enabled=false'.
 *
 * @author edmaputra
 * @since 0.0.1
 */
@Component
@ConditionalOnProperty(name = ["ilmiya.bootstrap.superadmin.enabled"], havingValue = "true", matchIfMissing = true)
class SuperAdminBootstrapSeeder(
    private val userRepository: UserRepository,
    private val manageUserUseCase: ManageUserUseCase,
    @Value("\${ilmiya.bootstrap.superadmin.email:superadmin@ilmiya.local}")
    private val superadminEmail: String = SUPERADMIN_EMAIL,
    @Value("\${ilmiya.bootstrap.superadmin.password:Password123!}")
    private val superadminPassword: String = SUPERADMIN_PASSWORD
) : ApplicationRunner {

    private val log = LoggerFactory.getLogger(javaClass)

    companion object {
        const val SUPERADMIN_EMAIL = "superadmin@ilmiya.local"
        const val SUPERADMIN_PASSWORD = "Password123!"
    }

    override fun run(args: ApplicationArguments) {
        if (!userRepository.existsByEmail(superadminEmail)) {
            log.info("Bootstrapping initial platform superadmin account: {}", superadminEmail)
            manageUserUseCase.createUser(
                UserCommands.CreateUserCommand(
                    superadminEmail,
                    superadminPassword,
                    "Platform Super Admin",
                    true
                )
            )
            log.info("Platform superadmin account successfully created: {}", superadminEmail)
        } else {
            log.info("Platform superadmin account already exists: {}. Skipping bootstrap.", superadminEmail)
        }
    }
}
