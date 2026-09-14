package io.github.edmaputra.ilmiya.common.domain

import java.time.Instant

/**
 * Contract representing an immutable domain event.
 *
 * @author edmaputra
 * @since 0.0.1
 */
interface DomainEvent {
    val occurredOn: Instant
}
