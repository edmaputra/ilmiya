package io.github.edmaputra.ilmiya.common.domain

import java.io.Serializable

/**
 * Generic domain entity identifier contract.
 *
 * @param T the underlying identifier type
 * @author edmaputra
 */
interface EntityId<T> : Serializable {
    val value: T
}
