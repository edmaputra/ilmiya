package io.github.edmaputra.ilmiya.app.school

import io.github.edmaputra.ilmiya.common.domain.TenantId
import io.github.edmaputra.ilmiya.common.dto.PageResponse
import io.github.edmaputra.ilmiya.core.school.application.ConfigureSchoolSettingsService
import io.github.edmaputra.ilmiya.core.school.application.ListTenantsService
import io.github.edmaputra.ilmiya.core.school.application.ManageTenantService
import io.github.edmaputra.ilmiya.core.school.application.RegisterSchoolService
import io.github.edmaputra.ilmiya.core.school.application.SchoolProfileService
import io.github.edmaputra.ilmiya.core.school.domain.School
import io.github.edmaputra.ilmiya.core.school.domain.port.`in`.ConfigureSchoolSettingsCommand
import io.github.edmaputra.ilmiya.core.school.domain.port.`in`.ConfigureSchoolSettingsUseCase
import io.github.edmaputra.ilmiya.core.school.domain.port.`in`.CreateTenantCommand
import io.github.edmaputra.ilmiya.core.school.domain.port.`in`.CreateTenantUseCase
import io.github.edmaputra.ilmiya.core.school.domain.port.`in`.DeleteTenantUseCase
import io.github.edmaputra.ilmiya.core.school.domain.port.`in`.GetSchoolProfileUseCase
import io.github.edmaputra.ilmiya.core.school.domain.port.`in`.GetTenantUseCase
import io.github.edmaputra.ilmiya.core.school.domain.port.`in`.ListTenantsUseCase
import io.github.edmaputra.ilmiya.core.school.domain.port.`in`.RegisterSchoolCommand
import io.github.edmaputra.ilmiya.core.school.domain.port.`in`.RegisterSchoolUseCase
import io.github.edmaputra.ilmiya.core.school.domain.port.`in`.UpdateSchoolProfileCommand
import io.github.edmaputra.ilmiya.core.school.domain.port.`in`.UpdateSchoolProfileUseCase
import io.github.edmaputra.ilmiya.core.school.domain.port.`in`.UpdateTenantCommand
import io.github.edmaputra.ilmiya.core.school.domain.port.`in`.UpdateTenantUseCase
import io.github.edmaputra.ilmiya.core.school.domain.port.out.SchoolEventPublisher
import io.github.edmaputra.ilmiya.core.school.domain.port.out.SchoolRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Transactional decorator for school registration use case.
 *
 * @author edmaputra
 * @since 0.0.1
 */
@Service
class TransactionalRegisterSchoolUseCase(
    schoolRepository: SchoolRepository,
    eventPublisher: SchoolEventPublisher
) : RegisterSchoolUseCase {

    private val delegate = RegisterSchoolService(schoolRepository, eventPublisher)

    @Transactional
    override fun register(command: RegisterSchoolCommand): School =
        delegate.register(command)
}

/**
 * Transactional decorator for school profile retrieval and update use cases.
 *
 * @author edmaputra
 * @since 0.0.1
 */
@Service
class TransactionalSchoolProfileUseCase(
    schoolRepository: SchoolRepository,
    eventPublisher: SchoolEventPublisher
) : GetSchoolProfileUseCase, UpdateSchoolProfileUseCase {

    private val delegate = SchoolProfileService(schoolRepository, eventPublisher)

    @Transactional(readOnly = true)
    override fun getProfile(tenantId: TenantId): School =
        delegate.getProfile(tenantId)

    @Transactional
    override fun updateProfile(tenantId: TenantId, command: UpdateSchoolProfileCommand): School =
        delegate.updateProfile(tenantId, command)
}

/**
 * Transactional decorator for school operational settings configuration use case.
 *
 * @author edmaputra
 * @since 0.0.1
 */
@Service
class TransactionalConfigureSchoolSettingsUseCase(
    schoolRepository: SchoolRepository,
    eventPublisher: SchoolEventPublisher
) : ConfigureSchoolSettingsUseCase {

    private val delegate = ConfigureSchoolSettingsService(schoolRepository, eventPublisher)

    @Transactional
    override fun configureSettings(tenantId: TenantId, command: ConfigureSchoolSettingsCommand): School =
        delegate.configureSettings(tenantId, command)
}

/**
 * Transactional decorator for tenant and school listing use case.
 *
 * @author edmaputra
 * @since 0.0.1
 */
@Service
class TransactionalListTenantsUseCase(
    schoolRepository: SchoolRepository
) : ListTenantsUseCase {

    private val delegate = ListTenantsService(schoolRepository)

    @Transactional(readOnly = true)
    override fun listTenants(page: Int, size: Int): PageResponse<School> =
        delegate.listTenants(page, size)
}

/**
 * Transactional decorator for tenant CRUD management use cases.
 *
 * @author edmaputra
 * @since 0.0.1
 */
@Service
class TransactionalManageTenantUseCase(
    schoolRepository: SchoolRepository,
    eventPublisher: SchoolEventPublisher
) : CreateTenantUseCase, GetTenantUseCase, UpdateTenantUseCase, DeleteTenantUseCase {

    private val delegate = ManageTenantService(schoolRepository, eventPublisher)

    @Transactional
    override fun createTenant(command: CreateTenantCommand): School =
        delegate.createTenant(command)

    @Transactional(readOnly = true)
    override fun getTenant(tenantId: TenantId): School =
        delegate.getTenant(tenantId)

    @Transactional
    override fun updateTenant(tenantId: TenantId, command: UpdateTenantCommand): School =
        delegate.updateTenant(tenantId, command)

    @Transactional
    override fun deleteTenant(tenantId: TenantId) =
        delegate.deleteTenant(tenantId)
}
