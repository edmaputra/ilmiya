package io.github.edmaputra.ilmiya.core.school.adapter.persistence

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.time.LocalDate

/**
 * JPA entity representing school institution record in the database.
 *
 * @author edmaputra
 */
@Entity
@Table(name = "schools")
class SchoolJpaEntity(
    @Id
    @Column(name = "id", length = 36, nullable = false)
    var id: String = "",

    @Column(name = "tenant_id", length = 50, nullable = false, unique = true)
    var tenantId: String = "",

    @Column(name = "name", length = 200, nullable = false)
    var name: String = "",

    @Column(name = "institution_code", length = 50, nullable = false, unique = true)
    var institutionCode: String = "",

    @Column(name = "government_reg_number", length = 50)
    var governmentRegistrationNumber: String? = null,

    @Column(name = "registration_type", length = 50)
    var registrationType: String? = null,

    @Column(name = "address", length = 500, nullable = false)
    var address: String = "",

    @Column(name = "contact_email", length = 150, nullable = false)
    var contactEmail: String = "",

    @Column(name = "contact_phone", length = 50, nullable = false)
    var contactPhone: String = "",

    @Column(name = "logo_url", length = 500)
    var logoUrl: String? = null,

    @Column(name = "accreditation_status", length = 50, nullable = false)
    var accreditationStatus: String = "UNACCREDITED",

    @Column(name = "accreditation_body", length = 100)
    var accreditationBody: String? = null,

    @Column(name = "accreditation_valid_until")
    var accreditationValidUntil: LocalDate? = null,

    @Column(name = "working_days", length = 100, nullable = false)
    var workingDays: String = "MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY",

    @Column(name = "grading_preference", length = 50, nullable = false)
    var gradingPreference: String = "NUMERIC_0_100",

    @Column(name = "term_layout", length = 50, nullable = false)
    var termLayout: String = "SEMESTER",

    @Column(name = "status", length = 50, nullable = false)
    var status: String = "ACTIVE",

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now()
)
