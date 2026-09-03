package io.github.edmaputra.ilmiya.common.exception

/**
 * Thrown when an operation requiring tenant identity is executed without an active tenant context.
 *
 * @author edmaputra
 */
class MissingTenantContextException(
    message: String = "A tenant context is required for this operation."
) : DomainException(message)
