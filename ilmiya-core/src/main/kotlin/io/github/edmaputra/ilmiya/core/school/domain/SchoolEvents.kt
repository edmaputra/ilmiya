package io.github.edmaputra.ilmiya.core.school.domain

import io.github.edmaputra.ilmiya.common.domain.DomainEvent
import io.github.edmaputra.ilmiya.common.domain.TenantId
import java.time.Instant

/**
 * Domain event published when a new school institution is registered and provisioned.
 *
 * @property schoolId the identifier of the registered school
 * @property tenantId the tenant boundary of the school
 * @property institutionCode unique institution short code
 * @property name the name of the school
 * @property occurredOn instant when the event occurred
 * @author edmaputra
 */
data class SchoolRegisteredEvent(
    val schoolId: SchoolId,
    val tenantId: TenantId,
    val institutionCode: String,
    val name: String,
    override val occurredOn: Instant = Instant.now()
) : DomainEvent

/**
 * Domain event published when school institutional profile information is updated.
 *
 * @property schoolId the identifier of the updated school
 * @property tenantId the tenant boundary of the school
 * @property name updated school name
 * @property occurredOn instant when the event occurred
 * @author edmaputra
 */
data class SchoolProfileUpdatedEvent(
    val schoolId: SchoolId,
    val tenantId: TenantId,
    val name: String,
    override val occurredOn: Instant = Instant.now()
) : DomainEvent

/**
 * Domain event published when school operational settings are modified.
 *
 * @property schoolId the identifier of the school
 * @property tenantId the tenant boundary of the school
 * @property occurredOn instant when the event occurred
 * @author edmaputra
 */
data class SchoolSettingsConfiguredEvent(
    val schoolId: SchoolId,
    val tenantId: TenantId,
    override val occurredOn: Instant = Instant.now()
) : DomainEvent
