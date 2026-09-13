package io.github.edmaputra.ilmiya.common.domain

import java.io.Serializable

/**
 * Generic domain entity identifier contract.
 *
 * @param T the underlying identifier type
 * @author edmaputra
 * @since 0.0.1
 */
interface EntityId<T> : Serializable {
    val value: T
}
