package io.github.edmaputra.ilmiya.common.domain

import java.time.Instant

interface DomainEvent {
    val occurredOn: Instant
}
