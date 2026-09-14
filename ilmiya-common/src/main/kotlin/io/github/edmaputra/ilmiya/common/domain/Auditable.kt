package io.github.edmaputra.ilmiya.common.domain

/**
 * Contract for domain entities that provide an audit trail representation.
 *
 * @author edmaputra
 * @since 0.0.1
 */
interface Auditable {
    fun auditableFields(): Map<String, Any?>
}
