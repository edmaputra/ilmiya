package io.github.edmaputra.ilmiya.core.security

import io.github.edmaputra.iam.domain.model.Permission
import io.github.edmaputra.iam.domain.repository.PermissionRepository
import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

/**
 * Application startup runner that seeds standard Ilmiya system permissions into the ed-iam catalog.
 *
 * Runs automatically upon application startup unless explicitly disabled via
 * `ilmiya.iam.permissions.seed.enabled=false`.
 *
 * Operates with [Ordered.HIGHEST_PRECEDENCE] to ensure all system permissions exist
 * before domain seeders or tenant role assignments are executed.
 *
 * @author edmaputra
 * @since 0.1.0
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@ConditionalOnProperty(name = ["ilmiya.iam.permissions.seed.enabled"], havingValue = "true", matchIfMissing = true)
class PermissionCatalogSeeder(
    private val permissionRepository: PermissionRepository
) : ApplicationRunner {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun run(args: ApplicationArguments) {
        seedCatalog()
    }

    /**
     * Iterates through [IlmiyaPermissionCatalog.ENTRIES] and saves any missing system permissions.
     *
     * @return the number of newly seeded permissions
     */
    fun seedCatalog(): Int {
        log.info("Checking Ilmiya Permission Catalog entries in ed-iam...")
        var seededCount = 0

        for (entry in IlmiyaPermissionCatalog.ENTRIES) {
            if (!permissionRepository.existsSystemPermissionByCode(entry.code)) {
                val systemPermission = Permission.createSystem(
                    entry.code,
                    entry.name,
                    entry.description,
                    entry.category
                )
                permissionRepository.save(systemPermission)
                seededCount++
            }
        }

        if (seededCount > 0) {
            log.info("Successfully seeded {} system permissions into ed-iam catalog.", seededCount)
        } else {
            log.info(
                "Ilmiya Permission Catalog is up to date ({} entries verified).",
                IlmiyaPermissionCatalog.ENTRIES.size
            )
        }

        return seededCount
    }
}
