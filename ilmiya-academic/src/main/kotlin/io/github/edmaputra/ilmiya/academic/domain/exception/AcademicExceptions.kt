package io.github.edmaputra.ilmiya.academic.domain.exception

import io.github.edmaputra.ilmiya.common.exception.DomainException

/**
 * Thrown when an academic year is not found.
 *
 * @author edmaputra
 * @since 0.0.1
 */
class AcademicYearNotFoundException(message: String) : DomainException(message)

/**
 * Thrown when an academic term is not found within an academic year.
 *
 * @author edmaputra
 * @since 0.0.1
 */
class AcademicTermNotFoundException(message: String) : DomainException(message)

/**
 * Thrown when academic year or term dates violate boundary or ordering invariants.
 *
 * @author edmaputra
 * @since 0.0.1
 */
class InvalidAcademicYearDatesException(message: String) : DomainException(message)

/**
 * Thrown when attempting to create an academic year with a name that already exists for the tenant.
 *
 * @author edmaputra
 * @since 0.0.1
 */
class DuplicateAcademicYearException(message: String) : DomainException(message)
