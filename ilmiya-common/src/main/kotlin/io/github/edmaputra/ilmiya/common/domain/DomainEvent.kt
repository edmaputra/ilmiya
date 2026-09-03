package io.github.edmaputra.ilmiya.common.domain

import java.time.Instant

/**
 * Contract representing an immutable domain event.
 *
 * @author edmaputra
 */
interface DomainEvent {
    val occurredOn: Instant
}
