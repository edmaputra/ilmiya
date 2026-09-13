package io.github.edmaputra.ilmiya.core.security

import io.github.edmaputra.iam.domain.exception.AccessDeniedException
import io.github.edmaputra.iam.domain.security.CurrentActor
import io.github.edmaputra.iam.domain.security.CurrentActorProvider
import java.util.UUID

/**
 * Idiomatic Kotlin extensions for actor authorization and scoping checks.
 *
 * @author edmaputra
 * @since 0.0.1
 */

/**
 * Asserts that the actor possesses the specified permission.
 * Throws [AccessDeniedException] if missing.
 */
fun CurrentActor.requirePermission(permission: String) {
    if (!hasPermission(permission)) {
        throw AccessDeniedException("Access denied: Actor lacks required permission '$permission'")
    }
}

/**
 * Asserts that the actor possesses at least one of the specified permissions.
 * Throws [AccessDeniedException] if none match.
 */
fun CurrentActor.requireAnyPermission(vararg permissions: String) {
    if (isPlatformSuperAdmin) return
    val matched = permissions.any { hasPermission(it) }
    if (!matched) {
        throw AccessDeniedException("Access denied: Actor lacks any of required permissions: ${permissions.joinToString()}")
    }
}

/**
 * Asserts that the actor possesses all of the specified permissions.
 * Throws [AccessDeniedException] if any are missing.
 */
fun CurrentActor.requireAllPermissions(vararg permissions: String) {
    if (isPlatformSuperAdmin) return
    val missing = permissions.filterNot { hasPermission(it) }
    if (missing.isNotEmpty()) {
        throw AccessDeniedException("Access denied: Actor is missing required permissions: ${missing.joinToString()}")
    }
}

/**
 * Asserts that the target scope node is within the actor's authorized scope subtree.
 * Throws [AccessDeniedException] if outside boundary.
 */
fun CurrentActor.requireScope(targetScopeNodeId: UUID) {
    if (!canAccessScope(targetScopeNodeId)) {
        throw AccessDeniedException("Access denied: Actor is outside authorized scope node '$targetScopeNodeId'")
    }
}

/**
 * Asserts that the target scope node (represented as UUID String) is within the actor's authorized scope subtree.
 */
fun CurrentActor.requireScope(targetScopeNodeId: String) {
    val uuid = try {
        UUID.fromString(targetScopeNodeId)
    } catch (_: IllegalArgumentException) {
        throw AccessDeniedException("Access denied: Invalid scope node UUID '$targetScopeNodeId'")
    }
    requireScope(uuid)
}

/**
 * Convenience accessor to retrieve current actor or throw an [AccessDeniedException].
 */
fun CurrentActorProvider.requireActor(): CurrentActor =
    currentActor().orElseThrow {
        AccessDeniedException("Access denied: Authenticated actor context is required.")
    }
