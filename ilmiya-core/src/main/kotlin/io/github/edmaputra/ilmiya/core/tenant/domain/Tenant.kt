package io.github.edmaputra.ilmiya.core.tenant.domain

import io.github.edmaputra.ilmiya.common.domain.AggregateRoot
import io.github.edmaputra.ilmiya.common.domain.Auditable
import io.github.edmaputra.ilmiya.common.domain.TenantId
import java.time.Instant

/**
 * Aggregate root representing an educational foundation, school board, or tenant boundary.
 * Contains and governs one or more educational school units.
 *
 * @property id unique identifier of the tenant foundation
 * @property name official name of the foundation (Yayasan)
 * @property legalEntityNumber government legal deed, tax ID (NPWP), or foundation registration code
 * @property registrationType type of legal entity (e.g. "YAYASAN", "FOUNDATION", "DISTRICT")
 * @property address primary administrative headquarters address
 * @property contactEmail primary administrative contact email
 * @property contactPhone primary administrative contact phone number
 * @property logoUrl optional URL pointing to foundation emblem or logo
 * @property status operational status of the foundation
 * @property createdAt timestamp of initial registration
 * @property updatedAt timestamp of latest modification
 * @author edmaputra
 * @since 0.0.1
 */
data class Tenant(
    override val id: TenantId,
    val name: String,
    val legalEntityNumber: String? = null,
    val registrationType: String? = null,
    val address: String,
    val contactEmail: String,
    val contactPhone: String,
    val logoUrl: String? = null,
    val status: TenantStatus = TenantStatus.ACTIVE,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now()
) : AggregateRoot<TenantId>(), Auditable {

    init {
        require(name.isNotBlank()) { "Tenant name must not be blank." }
        require(address.isNotBlank()) { "Tenant address must not be blank." }
        require(contactEmail.isNotBlank()) { "Contact email must not be blank." }
        require(contactPhone.isNotBlank()) { "Contact phone must not be blank." }
    }

    /**
     * Registers initial creation event.
     */
    fun markCreated(): Tenant {
        registerEvent(
            TenantCreatedEvent(
                tenantId = id,
                name = name,
                contactEmail = contactEmail
            )
        )
        return this
    }

    /**
     * Updates tenant profile details and registers an update domain event.
     */
    fun updateProfile(
        name: String,
        address: String,
        contactEmail: String,
        contactPhone: String,
        logoUrl: String? = this.logoUrl,
        legalEntityNumber: String? = this.legalEntityNumber,
        registrationType: String? = this.registrationType,
        status: TenantStatus = this.status
    ): Tenant {
        val updated = copy(
            name = name,
            address = address,
            contactEmail = contactEmail,
            contactPhone = contactPhone,
            logoUrl = logoUrl,
            legalEntityNumber = legalEntityNumber,
            registrationType = registrationType,
            status = status,
            updatedAt = Instant.now()
        )
        updated.registerEvent(
            TenantUpdatedEvent(
                tenantId = id,
                name = name,
                status = status
            )
        )
        return updated
    }

    /**
     * Changes operational status of the tenant foundation.
     */
    fun changeStatus(newStatus: TenantStatus): Tenant {
        if (this.status == newStatus) return this
        val updated = copy(
            status = newStatus,
            updatedAt = Instant.now()
        )
        updated.registerEvent(
            TenantStatusChangedEvent(
                tenantId = id,
                previousStatus = this.status,
                newStatus = newStatus
            )
        )
        return updated
    }

    override fun auditableFields(): Map<String, Any?> = mapOf(
        "id" to id.value,
        "name" to name,
        "legalEntityNumber" to legalEntityNumber,
        "registrationType" to registrationType,
        "contactEmail" to contactEmail,
        "status" to status.name
    )

    companion object {
        fun create(
            tenantId: TenantId,
            name: String,
            address: String,
            contactEmail: String,
            contactPhone: String,
            legalEntityNumber: String? = null,
            registrationType: String? = null,
            logoUrl: String? = null,
            status: TenantStatus = TenantStatus.ACTIVE
        ): Tenant = Tenant(
            id = tenantId,
            name = name,
            address = address,
            contactEmail = contactEmail,
            contactPhone = contactPhone,
            legalEntityNumber = legalEntityNumber,
            registrationType = registrationType,
            logoUrl = logoUrl,
            status = status
        ).markCreated()
    }
}
