package io.github.edmaputra.ilmiya.common.domain

/**
 * Contract for domain entities that provide an audit trail representation.
 *
 * @author edmaputra
 */
interface Auditable {
    fun auditableFields(): Map<String, Any?>
}
