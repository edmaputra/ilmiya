package io.github.edmaputra.ilmiya.academic.adapter.persistence

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import jakarta.persistence.Version
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

/**
 * JPA entity representing an academic calendar year and its child terms.
 *
 * @author edmaputra
 * @since 0.0.1
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

    @Version
    @Column(name = "version", nullable = false)
    var version: Long = 0L,

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now(),

    @Column(name = "created_by")
    var createdBy: UUID? = null,

    @Column(name = "updated_by")
    var updatedBy: UUID? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AcademicYearJpaEntity) return false
        return id.isNotBlank() && id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
    override fun toString(): String = "AcademicYearJpaEntity(id='$id', name='$name', tenantId='$tenantId')"
}
