package io.github.edmaputra.ilmiya.academic.domain

import io.github.edmaputra.ilmiya.common.domain.DomainEvent
import io.github.edmaputra.ilmiya.common.domain.TenantId
import java.time.Instant

/**
 * Domain event published when a new academic year is created.
 *
 * @property academicYearId identifier of the academic year
 * @property tenantId tenant boundary
 * @property name academic year name
 * @property occurredOn event occurrence timestamp
 * @author edmaputra
 */
data class AcademicYearCreatedEvent(
    val academicYearId: AcademicYearId,
    val tenantId: TenantId,
    val name: String,
    override val occurredOn: Instant = Instant.now()
) : DomainEvent

/**
 * Domain event published when an academic year is activated.
 *
 * @property academicYearId identifier of the activated year
 * @property tenantId tenant boundary
 * @property occurredOn event occurrence timestamp
 * @author edmaputra
 */
data class AcademicYearActivatedEvent(
    val academicYearId: AcademicYearId,
    val tenantId: TenantId,
    override val occurredOn: Instant = Instant.now()
) : DomainEvent

/**
 * Domain event published when a new term is added to an academic year.
 *
 * @property academicYearId identifier of parent academic year
 * @property termId identifier of added term
 * @property tenantId tenant boundary
 * @property occurredOn event occurrence timestamp
 * @author edmaputra
 */
data class AcademicTermAddedEvent(
    val academicYearId: AcademicYearId,
    val termId: AcademicTermId,
    val tenantId: TenantId,
    override val occurredOn: Instant = Instant.now()
) : DomainEvent
