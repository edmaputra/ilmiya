package io.github.edmaputra.ilmiya.core.school.application

import io.github.edmaputra.ilmiya.common.domain.TenantId
import io.github.edmaputra.ilmiya.core.school.domain.School
import io.github.edmaputra.ilmiya.core.school.domain.exception.SchoolNotFoundException
import io.github.edmaputra.ilmiya.core.school.domain.port.`in`.ConfigureSchoolSettingsCommand
import io.github.edmaputra.ilmiya.core.school.domain.port.`in`.ConfigureSchoolSettingsUseCase
import io.github.edmaputra.ilmiya.core.school.domain.port.out.SchoolEventPublisher
import io.github.edmaputra.ilmiya.core.school.domain.port.out.SchoolRepository

/**
 * Application service managing school operational settings modifications.
 *
 * @property schoolRepository persistence port for school entities
 * @property eventPublisher outbound publisher for school domain events
 * @author edmaputra
 */
class ConfigureSchoolSettingsService(
    private val schoolRepository: SchoolRepository,
    private val eventPublisher: SchoolEventPublisher
) : ConfigureSchoolSettingsUseCase {

    override fun configureSettings(tenantId: TenantId, command: ConfigureSchoolSettingsCommand): School {
        val school = schoolRepository.findByTenantId(tenantId)
            ?: throw SchoolNotFoundException("School profile for tenant '${tenantId.value}' was not found.")

        val updated = school.configureSettings(command.toOperationalSettings())
        val saved = schoolRepository.save(updated)
        eventPublisher.publishAll(updated.pullDomainEvents())
        return saved
    }
}
