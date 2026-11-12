package io.github.edmaputra.ilmiya.core.tenant.domain.port.out

import io.github.edmaputra.ilmiya.common.domain.DomainEvent

/**
 * Outbound port for publishing tenant foundation domain events.
 *
 * @author edmaputra
 * @since 0.0.1
 */
interface TenantEventPublisher {

    /**
     * Publishes a single domain event.
     */
    fun publish(event: DomainEvent)

    /**
     * Publishes multiple domain events.
     */
    fun publishAll(events: List<DomainEvent>) {
        events.forEach { publish(it) }
    }
}
