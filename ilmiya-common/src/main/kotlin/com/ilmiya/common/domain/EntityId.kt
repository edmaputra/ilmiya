package com.ilmiya.common.domain

import java.io.Serializable

interface EntityId<T> : Serializable {
    val value: T
}
