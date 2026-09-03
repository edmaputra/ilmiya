package com.ilmiya.common.domain

import java.time.Instant

interface DomainEvent {
    val occurredOn: Instant
}
