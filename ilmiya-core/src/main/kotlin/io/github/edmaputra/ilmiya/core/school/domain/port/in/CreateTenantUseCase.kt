package io.github.edmaputra.ilmiya.core.school.domain.port.`in`

import io.github.edmaputra.ilmiya.core.school.domain.School

/**
 * Inbound port contract for provisioning a new tenant.
 *
 * @author edmaputra
 * @since 0.0.1
 */
interface CreateTenantUseCase {

    /**
     * Provisions a new tenant and school institutional aggregate.
     *
     * @param command tenant registration command
     * @return persisted school aggregate representing the new tenant
     */
    fun createTenant(command: CreateTenantCommand): School
}
