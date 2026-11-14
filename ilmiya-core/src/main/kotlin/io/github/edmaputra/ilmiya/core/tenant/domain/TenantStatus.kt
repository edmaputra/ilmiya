package io.github.edmaputra.ilmiya.core.tenant.domain

/**
 * Operational lifecycle states for tenant organizations / foundations.
 *
 * @author edmaputra
 * @since 0.0.1
 */
enum class TenantStatus {
    ACTIVE,
    SUSPENDED,
    PENDING_ONBOARDING,
    ARCHIVED
}
