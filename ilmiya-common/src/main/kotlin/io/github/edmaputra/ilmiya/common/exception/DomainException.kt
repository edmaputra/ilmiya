package io.github.edmaputra.ilmiya.common.exception

/**
 * Base unchecked domain exception.
 *
 * @author edmaputra
 * @since 0.0.1
 */
open class DomainException(
    message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause)
