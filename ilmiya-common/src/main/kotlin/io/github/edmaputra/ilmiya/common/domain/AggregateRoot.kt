package io.github.edmaputra.ilmiya.common.domain

import java.util.Collections

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
