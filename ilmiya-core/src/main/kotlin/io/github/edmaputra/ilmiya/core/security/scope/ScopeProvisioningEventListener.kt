package io.github.edmaputra.ilmiya.core.security.scope

import io.github.edmaputra.ilmiya.core.school.domain.SchoolProfileUpdatedEvent
import io.github.edmaputra.ilmiya.core.school.domain.SchoolRegisteredEvent
import io.github.edmaputra.ilmiya.core.school.domain.SchoolStatus
import io.github.edmaputra.ilmiya.core.school.domain.port.out.SchoolRepository
import io.github.edmaputra.ilmiya.core.tenant.domain.TenantCreatedEvent
import io.github.edmaputra.ilmiya.core.tenant.domain.TenantStatus
import io.github.edmaputra.ilmiya.core.tenant.domain.TenantStatusChangedEvent
import io.github.edmaputra.ilmiya.core.tenant.domain.TenantUpdatedEvent
import io.github.edmaputra.ilmiya.core.tenant.domain.port.out.TenantRepository
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

/**
 * Event listener intercepting tenant and school domain lifecycle events to trigger
 * auto-provisioning of root and campus anchor scope nodes.
 *
 * @author edmaputra
 * @since 0.1.0
 */
@Component
class ScopeProvisioningEventListener(
    private val scopeProvisioningService: ScopeProvisioningService,
    private val tenantRepository: TenantRepository,
    private val schoolRepository: SchoolRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * Intercepts new tenant creation events and auto-provisions the tenant root scope node.
     */
    @EventListener
    fun onTenantCreated(event: TenantCreatedEvent) {
        log.debug("Received TenantCreatedEvent for tenantId: {}", event.tenantId.value)
        scopeProvisioningService.provisionTenantRootScope(event.tenantId, event.name)
    }

    /**
     * Intercepts tenant profile updates. If tenant is active, ensures root scope is present/updated.
     */
    @EventListener
    fun onTenantUpdated(event: TenantUpdatedEvent) {
        if (event.status == TenantStatus.ACTIVE) {
            log.debug("Received TenantUpdatedEvent for active tenantId: {}", event.tenantId.value)
            scopeProvisioningService.provisionTenantRootScope(event.tenantId, event.name)
        }
    }

    /**
     * Intercepts tenant status changes. When activated, provisions root scope.
     */
    @EventListener
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
     */
    @EventListener
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
     * Intercepts school profile updates. If school is active, ensures anchor scope is present/updated.
     */
    @EventListener
    fun onSchoolProfileUpdated(event: SchoolProfileUpdatedEvent) {
        log.debug("Received SchoolProfileUpdatedEvent for schoolId: {}, tenantId: {}", event.schoolId.value, event.tenantId.value)
        val school = schoolRepository.findById(event.schoolId)
            ?: schoolRepository.findByTenantId(event.tenantId)
        if (school != null && school.status == SchoolStatus.ACTIVE) {
            scopeProvisioningService.provisionCampusAnchorScope(
                tenantId = event.tenantId,
                schoolId = school.id,
                institutionCode = school.institutionCode,
                schoolName = event.name
            )
        }
    }
}
