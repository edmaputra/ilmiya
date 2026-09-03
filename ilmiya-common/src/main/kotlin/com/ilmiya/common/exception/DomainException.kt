package com.ilmiya.common.exception

open class DomainException(
    message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause)
