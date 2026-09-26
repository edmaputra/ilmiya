package io.github.edmaputra.ilmiya.core.school.domain.exception

import io.github.edmaputra.ilmiya.common.exception.DomainException

/**
 * Thrown when a requested school institution is not found.
 *
 * @author edmaputra
 * @since 0.0.1
 */
class SchoolNotFoundException(message: String) : DomainException(message)

/**
 * Thrown when a requested tenant boundary is not found.
 *
 * @author edmaputra
 * @since 0.0.1
 */
class TenantNotFoundException(message: String) : DomainException(message)

/**
 * Thrown when attempting to register a school with an institution code that is already in use.
 *
 * @author edmaputra
 * @since 0.0.1
 */
class DuplicateInstitutionCodeException(message: String) : DomainException(message)

/**
 * Thrown when attempting to register a tenant with an identifier that already exists.
 *
 * @author edmaputra
 * @since 0.0.1
 */
class DuplicateTenantException(message: String) : DomainException(message)
