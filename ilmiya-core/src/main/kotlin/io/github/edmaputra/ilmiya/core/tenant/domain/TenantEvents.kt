package io.github.edmaputra.ilmiya.core.tenant.domain

import io.github.edmaputra.ilmiya.common.domain.DomainEvent
import io.github.edmaputra.ilmiya.common.domain.TenantId
import java.time.Instant

/**
 * Domain event published when a new tenant foundation is created.
 */
data class TenantCreatedEvent(
    val tenantId: TenantId,
    val name: String,
    val contactEmail: String,
    override val occurredOn: Instant = Instant.now()
) : DomainEvent

/**
 * Domain event published when a tenant foundation profile is updated.
 */
data class TenantUpdatedEvent(
    val tenantId: TenantId,
    val name: String,
    val status: TenantStatus,
    override val occurredOn: Instant = Instant.now()
) : DomainEvent

/**
 * Domain event published when a tenant foundation status changes.
 */
data class TenantStatusChangedEvent(
    val tenantId: TenantId,
    val previousStatus: TenantStatus,
    val newStatus: TenantStatus,
    override val occurredOn: Instant = Instant.now()
) : DomainEvent
