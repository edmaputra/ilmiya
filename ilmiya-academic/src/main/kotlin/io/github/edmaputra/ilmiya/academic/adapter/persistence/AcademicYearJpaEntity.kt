package io.github.edmaputra.ilmiya.academic.adapter.persistence

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.time.Instant
import java.time.LocalDate

/**
 * JPA entity representing an academic calendar year and its child terms.
 *
 * @author edmaputra
 */
@Entity
@Table(name = "academic_years")
class AcademicYearJpaEntity(
    @Id
    @Column(name = "id", length = 36, nullable = false)
    var id: String = "",

    @Column(name = "tenant_id", length = 50, nullable = false)
    var tenantId: String = "",

    @Column(name = "name", length = 100, nullable = false)
    var name: String = "",

    @Column(name = "start_date", nullable = false)
    var startDate: LocalDate = LocalDate.now(),

    @Column(name = "end_date", nullable = false)
    var endDate: LocalDate = LocalDate.now(),

    @Column(name = "status", length = 50, nullable = false)
    var status: String = "PLANNED",

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "academic_year_id")
    var terms: MutableList<AcademicTermJpaEntity> = mutableListOf(),

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now()
)
