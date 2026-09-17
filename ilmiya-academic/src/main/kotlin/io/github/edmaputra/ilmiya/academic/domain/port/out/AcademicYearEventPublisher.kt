package io.github.edmaputra.ilmiya.academic.domain.port.out

import io.github.edmaputra.ilmiya.common.domain.DomainEvent

/**
 * Outbound domain event publishing port for academic domain events.
 *
 * @author edmaputra
 * @since 0.0.1
 */
interface AcademicYearEventPublisher {

    /**
     * Publishes a single domain event.
     *
     * @param event domain event to publish
     */
    fun publish(event: DomainEvent)

    /**
     * Publishes a collection of domain events in order.
     *
     * @param events domain events to publish
     */
    fun publishAll(events: Collection<DomainEvent>) {
        events.forEach { publish(it) }
    }
}
