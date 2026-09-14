package io.github.edmaputra.ilmiya.common.domain

import java.util.Collections

/**
 * Abstract base class for aggregate roots managing internal domain events.
 *
 * @param ID the entity identifier type
 * @author edmaputra
 * @since 0.0.1
 */
abstract class AggregateRoot<ID : EntityId<*>> {

    private val _domainEvents: MutableList<DomainEvent> = mutableListOf()

    protected fun registerEvent(event: DomainEvent) {
        _domainEvents.add(event)
    }

    fun pullDomainEvents(): List<DomainEvent> {
        val events = _domainEvents.toList()
        _domainEvents.clear()
        return Collections.unmodifiableList(events)
    }

    abstract val id: ID
}
