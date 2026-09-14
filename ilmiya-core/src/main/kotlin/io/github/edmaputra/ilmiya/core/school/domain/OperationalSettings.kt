package io.github.edmaputra.ilmiya.core.school.domain

import java.time.DayOfWeek

/**
 * Institutional operational settings controlling daily and term configurations.
 *
 * @property workingDays set of days when school operates
 * @property gradingPreference institutional grading score preference
 * @property termLayout default academic term structure
 * @author edmaputra
 */
data class OperationalSettings(
    val workingDays: Set<DayOfWeek> = setOf(
        DayOfWeek.MONDAY,
        DayOfWeek.TUESDAY,
        DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY,
        DayOfWeek.FRIDAY
    ),
    val gradingPreference: GradingPreference = GradingPreference.NUMERIC_0_100,
    val termLayout: TermLayout = TermLayout.SEMESTER
) {
    init {
        require(workingDays.isNotEmpty()) { "Working days must not be empty." }
    }
}
