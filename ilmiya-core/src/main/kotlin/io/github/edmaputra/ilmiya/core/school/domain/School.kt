package io.github.edmaputra.ilmiya.core.school.domain

import io.github.edmaputra.ilmiya.common.domain.AggregateRoot
import io.github.edmaputra.ilmiya.common.domain.Auditable
import io.github.edmaputra.ilmiya.common.domain.TenantAware
import io.github.edmaputra.ilmiya.common.domain.TenantId
import java.time.Instant

/**
 * Aggregate root representing an educational school institution and tenant boundary.
 * Enforces operational invariants, accreditation tracking, and settings configuration.
 *
 * @property id unique identifier of the school
 * @property tenantId multi-tenant boundary discriminator
 * @property name official school institution name
 * @property institutionCode unique institutional short code across the platform
 * @property governmentRegistrationNumber national or regional registration code (e.g., NPSN, NCES, URN)
 * @property registrationType type or jurisdiction of registration (e.g., "NPSN", "NCES", "STATE")
 * @property address physical campus address
 * @property contactEmail primary administrative contact email
 * @property contactPhone primary administrative contact phone number
 * @property logoUrl optional URI pointing to school emblem or logo image
 * @property accreditation current institutional accreditation details
 * @property operationalSettings daily and term operational configuration
 * @property status current operational status of the school
 * @property createdAt timestamp of initial registration
 * @property updatedAt timestamp of latest modification
 * @author edmaputra
 * @since 0.0.1
 */
data class School(
    override val id: SchoolId,
    override val tenantId: TenantId,
    val name: String,
    val institutionCode: String,
    val governmentRegistrationNumber: String? = null,
    val registrationType: String? = null,
    val address: String,
    val contactEmail: String,
    val contactPhone: String,
    val logoUrl: String? = null,
    val accreditation: Accreditation = Accreditation.UNACCREDITED,
    val operationalSettings: OperationalSettings = OperationalSettings(),
    val status: SchoolStatus = SchoolStatus.ACTIVE,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now()
) : AggregateRoot<SchoolId>(), TenantAware, Auditable {

    init {
        require(name.isNotBlank()) { "School name must not be blank." }
        require(institutionCode.isNotBlank()) { "Institution code must not be blank." }
        require(address.isNotBlank()) { "School address must not be blank." }
        require(contactEmail.isNotBlank()) { "Contact email must not be blank." }
        require(contactPhone.isNotBlank()) { "Contact phone must not be blank." }
    }

    /**
     * Records initial school registration event.
     */
    fun markRegistered(): School {
        registerEvent(
            SchoolRegisteredEvent(
                schoolId = id,
                tenantId = tenantId,
                institutionCode = institutionCode,
                name = name
            )
        )
        return this
    }

    /**
     * Updates school profile details and registers an audit domain event.
     */
    fun updateProfile(
        name: String,
        address: String,
        contactEmail: String,
        contactPhone: String,
        logoUrl: String? = this.logoUrl,
        governmentRegistrationNumber: String? = this.governmentRegistrationNumber,
        registrationType: String? = this.registrationType,
        accreditation: Accreditation = this.accreditation
    ): School {
        val updated = copy(
            name = name,
            address = address,
            contactEmail = contactEmail,
            contactPhone = contactPhone,
            logoUrl = logoUrl,
            governmentRegistrationNumber = governmentRegistrationNumber,
            registrationType = registrationType,
            accreditation = accreditation,
            updatedAt = Instant.now()
        )
        updated.registerEvent(
            SchoolProfileUpdatedEvent(
                schoolId = id,
                tenantId = tenantId,
                name = name
            )
        )
        return updated
    }

    /**
     * Updates school operational settings.
     */
    fun configureSettings(settings: OperationalSettings): School {
        val updated = copy(
            operationalSettings = settings,
            updatedAt = Instant.now()
        )
        updated.registerEvent(
            SchoolSettingsConfiguredEvent(
                schoolId = id,
                tenantId = tenantId
            )
        )
        return updated
    }

    override fun auditableFields(): Map<String, Any?> = mapOf(
        "schoolId" to id.value,
        "tenantId" to tenantId.value,
        "name" to name,
        "institutionCode" to institutionCode,
        "governmentRegistrationNumber" to governmentRegistrationNumber,
        "registrationType" to registrationType,
        "address" to address,
        "contactEmail" to contactEmail,
        "contactPhone" to contactPhone,
        "accreditationStatus" to accreditation.status,
        "status" to status.name
    )

    companion object {
        fun create(
            id: SchoolId = SchoolId.generate(),
            tenantId: TenantId,
            name: String,
            institutionCode: String,
            governmentRegistrationNumber: String? = null,
            registrationType: String? = null,
            address: String,
            contactEmail: String,
            contactPhone: String,
            logoUrl: String? = null,
            accreditation: Accreditation = Accreditation.UNACCREDITED,
            operationalSettings: OperationalSettings = OperationalSettings()
        ): School {
            val school = School(
                id = id,
                tenantId = tenantId,
                name = name,
                institutionCode = institutionCode.trim().uppercase(),
                governmentRegistrationNumber = governmentRegistrationNumber?.trim(),
                registrationType = registrationType?.trim()?.uppercase(),
                address = address,
                contactEmail = contactEmail.trim().lowercase(),
                contactPhone = contactPhone.trim(),
                logoUrl = logoUrl,
                accreditation = accreditation,
                operationalSettings = operationalSettings
            )
            return school.markRegistered()
        }
    }
}
