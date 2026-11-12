package io.github.edmaputra.ilmiya.core.tenant.domain.exception

/**
 * Thrown when an operation targets a tenant foundation that does not exist.
 */
class TenantNotFoundException(message: String) : RuntimeException(message)

/**
 * Thrown when attempting to register a tenant with an ID that already exists.
 */
class DuplicateTenantException(message: String) : RuntimeException(message)
