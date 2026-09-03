# Multi-Tenancy & Audit Trail Rules

This document outlines the strict platform requirements for multi-tenancy and audit trailing in Ilmiya.

---

## 1. Multi-Tenancy Architecture

Multi-tenancy is a foundational architectural pillar. The system operates on a **shared application, shared database, tenant discriminator column** model.

### 1.1 Tenant Context Propagation
- **Tenant Context**: The tenant context is bound to the request scope via `TenantContext`.
- **Mandatory Scoping**: Every business operation on tenant-owned data must be executed within an active `TenantContext` containing a valid `TenantId`.
- **Fail Fast on Missing Context**: If an operation requiring tenant identity is called without an active context, throw a specific domain exception.

### 1.2 Tenant-Aware Persistence
- **Entity Ownership**: All tenant-specific JPA entities must include the tenant discriminator column (`tenant_id`) and implement `TenantAware`.
- **Query Scoping**: Repository implementations in persistence adapters MUST automatically filter queries by the current `TenantId`. Never allow un-scoped cross-tenant select or update queries.
- **Sequence Generation**: Document sequence numbers, admission numbers, and invoice numbers must be generated per tenant.

```kotlin
// ✅ GOOD: Tenant-scoped entity with discriminator
@Entity
@Table(name = "school_settings")
class SchoolSettingEntity(
    @EmbeddedId
    var id: SchoolSettingIdEntity? = null, // contains tenantId and settingKey
    @Column(nullable = false)
    var value: String = ""
)
```

---

## 2. Audit Trail System

Audit trailing is a platform capability implemented via domain events and append-only persistence.

### 2.1 The `Auditable` Interface
- Domain entities that require tracking must implement `Auditable`:
  ```kotlin
  interface Auditable {
      fun auditableFields(): Map<String, Any?>
  }
  ```
- Return safe state representations in `auditableFields()` without exposing sensitive data (passwords, tokens, or restricted PII).

### 2.2 Domain Event Publishing
- Core services must publish structured domain events upon state mutations (e.g. `StudentAdmitted`, `CourseCreated`) via outbound port `*EventPublisher`.
- Domain events should encapsulate:
  - Tenant ID (`TenantId`).
  - Actor info (actor ID and role).
  - Correlation ID.
  - Event timestamp (`occurredOn: Instant`).
  - Snapshot or delta of the changed entity.

### 2.3 Append-Only Audit Persistence
- Audit records stored in persistence adapters are strictly **append-only**.
- Normal application workflows must never update or delete existing audit entries.
- Cross-tenant audit viewing is reserved exclusively for platform administrators, not tenant users.
