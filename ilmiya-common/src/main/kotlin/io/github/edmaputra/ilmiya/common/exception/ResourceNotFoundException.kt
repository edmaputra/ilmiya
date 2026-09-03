package io.github.edmaputra.ilmiya.common.exception

/**
 * Domain exception thrown when a requested resource is not found.
 *
 * @author edmaputra
 */
class ResourceNotFoundException(
    resourceName: String,
    identifier: Any
) : DomainException("$resourceName with identifier '$identifier' was not found")
