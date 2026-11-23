package io.github.edmaputra.ilmiya.core.security.scope

import io.github.edmaputra.ilmiya.core.school.domain.SchoolProfileUpdatedEvent
import io.github.edmaputra.ilmiya.core.school.domain.SchoolRegisteredEvent
import io.github.edmaputra.ilmiya.core.tenant.domain.TenantCreatedEvent
import io.github.edmaputra.ilmiya.core.tenant.domain.TenantStatus
import io.github.edmaputra.ilmiya.core.tenant.domain.TenantStatusChangedEvent
import io.github.edmaputra.ilmiya.core.tenant.domain.TenantUpdatedEvent
import io.github.edmaputra.ilmiya.core.tenant.domain.port.out.TenantRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

/**
 * Event listener intercepting tenant and school domain lifecycle events to trigger
 * auto-provisioning of root and campus anchor scope nodes.
 *
 * All handlers fire **after the publisher's transaction commits** via [TransactionalEventListener]
 * with [TransactionPhase.AFTER_COMMIT], and each runs in its own independent transaction
 * ([Propagation.REQUIRES_NEW]). This guarantees:
 * - Scope node creation never rolls back a school registration or tenant creation.
 * - Scope node creation only runs when the primary record is safely persisted.
 *
 * @author edmaputra
 * @since 0.1.0
 */
@Component
class ScopeProvisioningEventListener(
    private val scopeProvisioningService: ScopeProvisioningService,
    private val tenantRepository: TenantRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * Intercepts new tenant creation events and auto-provisions the tenant root scope node.
     * Fires after the tenant creation transaction commits.
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun onTenantCreated(event: TenantCreatedEvent) {
        log.debug("Received TenantCreatedEvent for tenantId: {}", event.tenantId.value)
        scopeProvisioningService.provisionTenantRootScope(event.tenantId, event.name)
    }

    /**
     * Intercepts tenant profile updates. If tenant is active, ensures root scope is present/updated.
     * Fires after the tenant update transaction commits.
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun onTenantUpdated(event: TenantUpdatedEvent) {
        if (event.status == TenantStatus.ACTIVE) {
            log.debug("Received TenantUpdatedEvent for active tenantId: {}", event.tenantId.value)
            scopeProvisioningService.provisionTenantRootScope(event.tenantId, event.name)
        }
    }

    /**
     * Intercepts tenant status changes. When activated, provisions root scope.
     * Fires after the tenant status change transaction commits.
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun onTenantStatusChanged(event: TenantStatusChangedEvent) {
        if (event.newStatus == TenantStatus.ACTIVE) {
            log.debug("Received TenantStatusChangedEvent (ACTIVE) for tenantId: {}", event.tenantId.value)
            val tenant = tenantRepository.findById(event.tenantId)
            val tenantName = tenant?.name ?: "Tenant"
            scopeProvisioningService.provisionTenantRootScope(event.tenantId, tenantName)
        }
    }

    /**
     * Intercepts school registration events and auto-provisions the campus anchor scope node.
     * Fires after the school registration transaction commits.
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun onSchoolRegistered(event: SchoolRegisteredEvent) {
        log.debug("Received SchoolRegisteredEvent for schoolId: {}, tenantId: {}", event.schoolId.value, event.tenantId.value)
        scopeProvisioningService.provisionCampusAnchorScope(
            tenantId = event.tenantId,
            schoolId = event.schoolId,
            institutionCode = event.institutionCode,
            schoolName = event.name
        )
    }

    /**
     * Intercepts school profile updates and ensures the campus anchor scope is synced.
     * Fires after the school update transaction commits.
     *
     * The [SchoolProfileUpdatedEvent] carries [SchoolProfileUpdatedEvent.institutionCode] directly,
     * eliminating the need for an extra database lookup to resolve the school entity.
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun onSchoolProfileUpdated(event: SchoolProfileUpdatedEvent) {
        log.debug("Received SchoolProfileUpdatedEvent for schoolId: {}, tenantId: {}", event.schoolId.value, event.tenantId.value)
        scopeProvisioningService.provisionCampusAnchorScope(
            tenantId = event.tenantId,
            schoolId = event.schoolId,
            institutionCode = event.institutionCode,
            schoolName = event.name
        )
    }
}
