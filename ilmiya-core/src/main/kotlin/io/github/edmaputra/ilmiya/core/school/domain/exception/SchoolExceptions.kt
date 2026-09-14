package io.github.edmaputra.ilmiya.core.school.domain.exception

import io.github.edmaputra.ilmiya.common.exception.DomainException

/**
 * Thrown when a requested school institution is not found.
 *
 * @author edmaputra
 */
class SchoolNotFoundException(message: String) : DomainException(message)

/**
 * Thrown when attempting to register a school with an institution code that is already in use.
 *
 * @author edmaputra
 */
class DuplicateInstitutionCodeException(message: String) : DomainException(message)
