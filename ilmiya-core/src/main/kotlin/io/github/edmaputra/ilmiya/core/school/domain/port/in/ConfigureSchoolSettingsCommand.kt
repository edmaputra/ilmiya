package io.github.edmaputra.ilmiya.core.school.domain.port.`in`

import io.github.edmaputra.ilmiya.core.school.domain.GradingPreference
import io.github.edmaputra.ilmiya.core.school.domain.OperationalSettings
import io.github.edmaputra.ilmiya.core.school.domain.TermLayout
import java.time.DayOfWeek

/**
 * Command containing payload to configure school operational settings.
 *
 * @property workingDays set of operating days of the week
 * @property gradingPreference institutional grading score preference
 * @property termLayout default academic term structure
 * @author edmaputra
 */
data class ConfigureSchoolSettingsCommand(
    val workingDays: Set<DayOfWeek>,
    val gradingPreference: GradingPreference,
    val termLayout: TermLayout
) {
    init {
        require(workingDays.isNotEmpty()) { "Working days must not be empty." }
    }

    fun toOperationalSettings(): OperationalSettings = OperationalSettings(
        workingDays = workingDays,
        gradingPreference = gradingPreference,
        termLayout = termLayout
    )
}
