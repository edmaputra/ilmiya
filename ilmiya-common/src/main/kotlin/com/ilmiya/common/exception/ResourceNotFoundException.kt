package com.ilmiya.common.exception

class ResourceNotFoundException(
    resourceName: String,
    identifier: Any
) : DomainException("$resourceName with identifier '$identifier' was not found")
