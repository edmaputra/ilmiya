# How-To: School Registration & Multi-Tenant Onboarding

This guide walks through registering a new educational institution and provisioning its isolated tenant boundary in **Ilmiya**.

---

## Overview

In Ilmiya, multi-tenancy is logical and enforced at the application and database layers:
- **Tenant Discriminator**: Every tenant has a unique identifier (e.g. `al-azhar-jakarta`, `smk-taruna-01`).
- **Data Partitioning**: All tenant-specific tables (such as `core_schools`, `academic_years`, `academic_terms`, and future student/finance records) contain a `tenant_id` column.
- **Context Propagation**: The multi-tenant boundary is bound per request using Java 25 `ScopedValue` via `TenantContext` (`TenantContextFilter`), preventing thread-local state leakage across requests.
- **Registration Endpoint**: `POST /api/v1/schools` requires `school:registration:create` permission to provision school institutions and tenant boundaries.

---

## Step 1: Choose Tenant Identifier & Institution Code

Before registering a school, establish standard identifiers:
- **`tenantId`**: Alphanumeric string with hyphens or underscores (e.g. `sma-nusantara-1`). Must be unique across the platform.
- **`institutionCode`**: School reference code or internal acronym (e.g. `SMAN-1-JKT`).
- **`governmentRegistrationNumber`** *(Optional)*: Government identification number such as Indonesia's NPSN (Nomor Pokok Sekolah Nasional) or equivalent Ministry of Education ID.

```text
Format Recommendations:
Tenant ID:        lowercase-with-hyphens (e.g. smk-bina-bangsa)
Institution Code: UPPERCASE-ALPHANUMERIC (e.g. SMK-BB-01)
NPSN:             8-digit numeric string (e.g. 20101234)
```

---

## Step 2: Register the School via Secured API

Send a `POST` request to `/api/v1/schools` with authorization token carrying `school:registration:create`:

```bash
curl -i -X POST http://localhost:8080/api/v1/schools \
  -H "Authorization: Bearer <JWT_ACCESS_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "tenantId": "sma-nusantara-1",
    "name": "SMA Nusantara 1 Jakarta",
    "institutionCode": "SMAN-1-JKT",
    "governmentRegistrationNumber": "20104567",
    "registrationType": "SENIOR_HIGH_SCHOOL",
    "address": "Jl. Merdeka No. 45, Gambir, Jakarta Pusat",
    "contactEmail": "info@smanusantara1.sch.id",
    "contactPhone": "+62-21-5550199",
    "logoUrl": "https://storage.ilmiya.sch.id/logos/sma-nusantara-1.png",
    "accreditationStatus": "A",
    "accreditationBody": "BAN-S/M",
    "accreditationValidUntil": "2028-12-31",
    "workingDays": ["MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"],
    "gradingPreference": "NUMERIC_0_100",
    "termLayout": "SEMESTER"
  }'
```

**Expected Response (`201 Created`):**
```http
HTTP/1.1 201 Created
Location: /api/v1/schools/profile
Content-Type: application/json

{
  "id": "7fa189c2-3e28-4e11-8e01-987d60a1cb32",
  "tenantId": "sma-nusantara-1",
  "name": "SMA Nusantara 1 Jakarta",
  "institutionCode": "SMAN-1-JKT",
  "governmentRegistrationNumber": "20104567",
  "registrationType": "SENIOR_HIGH_SCHOOL",
  "address": "Jl. Merdeka No. 45, Gambir, Jakarta Pusat",
  "contactEmail": "info@smanusantara1.sch.id",
  "contactPhone": "+62-21-5550199",
  "logoUrl": "https://storage.ilmiya.sch.id/logos/sma-nusantara-1.png",
  "accreditationStatus": "A",
  "accreditationBody": "BAN-S/M",
  "accreditationValidUntil": "2028-12-31",
  "workingDays": ["MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"],
  "gradingPreference": "NUMERIC_0_100",
  "termLayout": "SEMESTER",
  "status": "ACTIVE",
  "createdAt": "2026-09-22T09:00:00Z",
  "updatedAt": "2026-09-22T09:00:00Z"
}
```

---

## Step 3: Manage Tenants via Secured CRUD API

Authorized administrators can perform full lifecycle CRUD management on tenants via `/api/v1/tenants` secured with fine-grained permissions:

| Operation | Endpoint | Required Permission | Description |
|---|---|---|---|
| **Create** | `POST /api/v1/tenants` | `tenant:management:create` | Provision a new tenant and institutional boundary |
| **List** | `GET /api/v1/tenants` | `tenant:management:read` | Paginated listing of all platform tenants |
| **Get** | `GET /api/v1/tenants/{tenantId}` | `tenant:management:read` | Retrieve tenant details by identifier |
| **Update** | `PUT /api/v1/tenants/{tenantId}` | `tenant:management:update` | Update tenant details and lifecycle status |
| **Delete** | `DELETE /api/v1/tenants/{tenantId}` | `tenant:management:delete` | Delete a tenant boundary |

*(Note: For institutional directories, `GET /api/v1/schools` is secured with `school:profile:read` permission).*

### Example: Retrieve Single Tenant
```bash
curl -s -X GET http://localhost:8080/api/v1/tenants/sma-nusantara-1 \
  -H "Authorization: Bearer <JWT_ACCESS_TOKEN>" | jq
```

### Example: Update Tenant Status
```bash
curl -i -X PUT http://localhost:8080/api/v1/tenants/sma-nusantara-1 \
  -H "Authorization: Bearer <JWT_ACCESS_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "SMA Nusantara 1 Jakarta",
    "address": "Jl. Merdeka No. 45, Jakarta",
    "contactEmail": "info@smanusantara1.sch.id",
    "contactPhone": "+62-21-5550199",
    "status": "ACTIVE"
  }'
```

### Example: Delete Tenant
```bash
curl -i -X DELETE http://localhost:8080/api/v1/tenants/sma-nusantara-1 \
  -H "Authorization: Bearer <JWT_ACCESS_TOKEN>"
```

---

## Step 4: Verify Persistence in the Database

Connect to the PostgreSQL instance and verify the record in `core_schools`:

```sql
SELECT id, tenant_id, name, institution_code, status, accreditation_status 
FROM core_schools 
WHERE tenant_id = 'sma-nusantara-1';
```

**Output:**
```
                  id                  |    tenant_id    |           name           | institution_code | status | accreditation_status 
--------------------------------------+-----------------+--------------------------+------------------+--------+----------------------
 7fa189c2-3e28-4e11-8e01-987d60a1cb32 | sma-nusantara-1 | SMA Nusantara 1 Jakarta  | SMAN-1-JKT       | ACTIVE | A
(1 row)
```

---

## Step 5: Retrieve the Institutional Profile

Secured endpoints require both an authenticated session and the tenant context header `X-Tenant-ID`. The profile endpoint resolves the school associated with the active tenant.

```bash
curl -s -X GET http://localhost:8080/api/v1/schools/profile \
  -H "Authorization: Bearer <JWT_ACCESS_TOKEN>" \
  -H "X-Tenant-ID: sma-nusantara-1" | jq
```

> [!NOTE]
> The caller must possess the `school:profile:read` permission. See [02. Authentication, IAM & Multi-Tenant Context Scoping](02-authentication-and-iam.md) for instructions on acquiring tokens.

---

## Step 6: Update School Profile Details

Institutional administrators can update contact details, address, logo, and accreditation via `PUT /api/v1/schools/profile`:

```bash
curl -i -X PUT http://localhost:8080/api/v1/schools/profile \
  -H "Authorization: Bearer <JWT_ACCESS_TOKEN>" \
  -H "X-Tenant-ID: sma-nusantara-1" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "SMA Nusantara 1 Jakarta (Unggulan)",
    "address": "Jl. Merdeka No. 45-47, Gambir, Jakarta Pusat",
    "contactEmail": "admin@smanusantara1.sch.id",
    "contactPhone": "+62-21-5550200",
    "logoUrl": "https://storage.ilmiya.sch.id/logos/sma-nusantara-1-v2.png",
    "governmentRegistrationNumber": "20104567",
    "registrationType": "SENIOR_HIGH_SCHOOL",
    "accreditationStatus": "A",
    "accreditationBody": "BAN-S/M",
    "accreditationValidUntil": "2029-12-31"
  }'
```

**Expected Response (`200 OK`):**
Returns the updated `SchoolResponse` payload with an incremented `version` and new `updatedAt` timestamp.

---

## Step 7: Verify Multi-Tenant Isolation

To verify strict tenant isolation:
1. Make a request with a non-existent or different tenant header:
```bash
curl -i -X GET http://localhost:8080/api/v1/schools/profile \
  -H "Authorization: Bearer <JWT_ACCESS_TOKEN>" \
  -H "X-Tenant-ID: another-school"
```

**Expected Response (`404 Not Found`):**
```json
{
  "timestamp": "2026-09-22T09:10:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "School with ID 'another-school' was not found"
}
```

2. Attempt to register a duplicate tenant ID:
```bash
curl -i -X POST http://localhost:8080/api/v1/schools \
  -H "Content-Type: application/json" \
  -d '{"tenantId": "sma-nusantara-1", ...}'
```

**Expected Response (`409 Conflict`):**
```json
{
  "timestamp": "2026-09-22T09:11:00Z",
  "status": 409,
  "error": "Conflict",
  "message": "School with tenant 'sma-nusantara-1' is already registered"
}
```

---

## Troubleshooting & Edge Cases

| Symptom / Error | Cause | Resolution |
|-----------------|-------|------------|
| `400 Bad Request: MissingTenantContextException` | Request sent without `X-Tenant-ID` header to a tenant-scoped endpoint. | Add header `X-Tenant-ID: <tenant-id>`. |
| `409 Conflict: School with institution code ... already exists` | The `institutionCode` must be globally unique across all schools. | Choose a unique code. |
| `403 Forbidden` | The user's JWT lacks `school:profile:read` or `school:profile:write` permission. | Assign the appropriate role or permission in ed-iam. |
