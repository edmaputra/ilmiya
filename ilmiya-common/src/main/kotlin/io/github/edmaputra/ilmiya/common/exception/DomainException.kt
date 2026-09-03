package io.github.edmaputra.ilmiya.common.exception

/**
 * Base unchecked domain exception.
 *
 * @author edmaputra
 */
open class DomainException(
    message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause)
