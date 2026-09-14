package io.github.edmaputra.ilmiya.core.school.domain.port.`in`

import io.github.edmaputra.ilmiya.common.domain.TenantId
import io.github.edmaputra.ilmiya.core.school.domain.School

/**
 * Inbound port for configuring school operational settings.
 *
 * @author edmaputra
 */
interface ConfigureSchoolSettingsUseCase {

    /**
     * Configures operational settings for the specified school tenant.
     *
     * @param tenantId tenant identifier
     * @param command operational settings parameters
     * @return the updated school aggregate
     * @throws io.github.edmaputra.ilmiya.core.school.domain.exception.SchoolNotFoundException if no school exists for the tenant
     */
    fun configureSettings(tenantId: TenantId, command: ConfigureSchoolSettingsCommand): School
}
