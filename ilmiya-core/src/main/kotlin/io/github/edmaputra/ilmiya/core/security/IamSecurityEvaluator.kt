package io.github.edmaputra.ilmiya.core.security

import io.github.edmaputra.iam.domain.security.CurrentActorProvider
import org.springframework.stereotype.Component
import java.util.UUID

/**
 * Spring Security SpEL evaluator bean ("@iam") for declarative method-level and endpoint authorization.
 *
 * Usage:
 * ```kotlin
 * @PreAuthorize("@iam.hasPermission('STUDENT_PROFILE_READ')")
 * @PreAuthorize("@iam.hasPermission('ATTENDANCE_RECORD') and @iam.canAccessScope(#classScopeId)")
 * ```
 *
 * @author edmaputra
 */
@Component("iam")
class IamSecurityEvaluator(
    private val currentActorProvider: CurrentActorProvider
) {

    /**
     * Checks if the currently authenticated actor has the specified permission.
     * Always returns true for platform superadmins.
     */
    fun hasPermission(permission: String): Boolean =
        currentActorProvider.currentActor()
            .map { it.hasPermission(permission) }
            .orElse(false)

    /**
     * Checks if the currently authenticated actor has any of the specified permissions.
     */
    fun hasAnyPermission(vararg permissions: String): Boolean =
        currentActorProvider.currentActor()
            .map { actor -> permissions.any { actor.hasPermission(it) } }
            .orElse(false)

    /**
     * Checks if the currently authenticated actor has all of the specified permissions.
     */
    fun hasAllPermissions(vararg permissions: String): Boolean =
        currentActorProvider.currentActor()
            .map { actor -> permissions.all { actor.hasPermission(it) } }
            .orElse(false)

    /**
     * Checks if the currently authenticated actor has access to the target organizational scope node.
     * Platform superadmins and tenant-wide actors can access all scope nodes within their tenant.
     */
    fun canAccessScope(scopeNodeId: UUID): Boolean =
        currentActorProvider.currentActor()
            .map { it.canAccessScope(scopeNodeId) }
            .orElse(false)

    /**
     * Convenience method checking scope access with String representation of UUID.
     */
    fun canAccessScope(scopeNodeId: String): Boolean =
        try {
            canAccessScope(UUID.fromString(scopeNodeId))
        } catch (_: IllegalArgumentException) {
            false
        }

    /**
     * Checks if the current actor is a platform superadmin.
     */
    fun isPlatformSuperAdmin(): Boolean =
        currentActorProvider.currentActor()
            .map { it.isPlatformSuperAdmin }
            .orElse(false)

    /**
     * Checks if the current actor has tenant-wide permissions without scope node restrictions.
     */
    fun isTenantWide(): Boolean =
        currentActorProvider.currentActor()
            .map { it.isTenantWide }
            .orElse(false)

    /**
     * Returns the current authenticated actor's userId, or null if unauthenticated.
     */
    fun getUserId(): UUID? =
        currentActorProvider.currentActor().map { it.userId() }.orElse(null)

    /**
     * Returns the current authenticated actor's email, or null if unauthenticated.
     */
    fun getEmail(): String? =
        currentActorProvider.currentActor().map { it.email() }.orElse(null)

    /**
     * Returns the current authenticated actor's tenantId, or null if unauthenticated or platform-wide.
     */
    fun getTenantId(): UUID? =
        currentActorProvider.currentActor().map { it.tenantId() }.orElse(null)
}
