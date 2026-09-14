package io.github.edmaputra.ilmiya.academic.adapter.persistence

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDate

/**
 * JPA entity representing an academic term/semester.
 *
 * @author edmaputra
 */
@Entity
@Table(name = "academic_terms")
class AcademicTermJpaEntity(
    @Id
    @Column(name = "id", length = 36, nullable = false)
    var id: String = "",

    @Column(name = "academic_year_id", length = 36, nullable = false)
    var academicYearId: String = "",

    @Column(name = "name", length = 100, nullable = false)
    var name: String = "",

    @Column(name = "term_type", length = 50, nullable = false)
    var termType: String = "",

    @Column(name = "display_name", length = 150, nullable = false)
    var displayName: String = "",

    @Column(name = "start_date", nullable = false)
    var startDate: LocalDate = LocalDate.now(),

    @Column(name = "end_date", nullable = false)
    var endDate: LocalDate = LocalDate.now(),

    @Column(name = "status", length = 50, nullable = false)
    var status: String = "PLANNED"
)
