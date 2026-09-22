# How-To: Authentication, IAM & Multi-Tenant Context Scoping

This guide walks through authenticating users, managing permissions, and propagating tenant context securely in **Ilmiya** using the `ed-iam` library and Java 25 scoped values.

---

## Overview

Ilmiya leverages `ed-iam` for stateless authentication, Role-Based Access Control (RBAC), and fine-grained permission authorization:
- **Stateless JWT**: Users authenticate and receive HMAC-SHA256 signed JSON Web Tokens containing their user ID, active tenant ID, and granted permission set.
- **Tenant Context Filter**: The incoming `X-Tenant-ID` HTTP header is verified and bound to Java 25's `ScopedValue<TenantId>` via `TenantContext`.
- **Method-Level Security**: Controllers enforce permissions using `@RequirePermission("domain:resource:action")`.
- **Domain Wildcards**: Permissions support wildcards such as `school:*` or `academic:*` for hierarchical role assignments.

```
Incoming Request
      │
      ▼
┌─────────────────────────┐
│   TenantContextFilter   │───► Binds X-Tenant-ID to Java 25 ScopedValue (TenantContext)
└───────────┬─────────────┘
            ▼
┌─────────────────────────┐
│ JwtAuthenticationFilter │───► Validates Bearer Token & establishes SecurityContext
└───────────┬─────────────┘
            ▼
┌─────────────────────────┐
│   @RequirePermission    │───► Verifies required permission (e.g. school:profile:read)
└───────────┬─────────────┘
            ▼
   Controller Execution
```

---

## Step 1: Authenticate via ed-iam

Authenticate by sending credentials to `/api/v1/auth/login`:

```bash
curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "principal@smanusantara1.sch.id",
    "password": "Password123!",
    "tenantId": "sma-nusantara-1"
  }' | jq
```

**Expected Response (`200 OK`):**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "d8e3b56a-12fa-45b9-b873-10d65b77e812",
  "tokenType": "Bearer",
  "expiresIn": 3600,
  "user": {
    "id": "e0a7df84-954e-4f01-b58f-287b32a2c1f9",
    "username": "principal@smanusantara1.sch.id",
    "tenantId": "sma-nusantara-1",
    "roles": ["SCHOOL_ADMIN"],
    "permissions": [
      "school:profile:read",
      "school:profile:write",
      "school:settings:manage",
      "academic:*"
    ]
  }
}
```

---

## Step 2: Formulate Authenticated Requests with `X-Tenant-ID`

Every request to a secured endpoint in Ilmiya must carry two headers:
1. `Authorization`: `Bearer <accessToken>`
2. `X-Tenant-ID`: The tenant identifier (e.g. `sma-nusantara-1`)

```bash
curl -i -X GET http://localhost:8080/api/v1/schools/profile \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -H "X-Tenant-ID: sma-nusantara-1"
```

> [!IMPORTANT]
> The `X-Tenant-ID` header must match the tenant boundary of the resource being manipulated. If omitted, the request fails with `400 Bad Request: Missing tenant context`.

---

## Step 3: Understanding Java 25 `ScopedValue` Context Propagation

Traditional Spring applications use `ThreadLocal` to propagate tenant IDs. However, in modern Java runtime environments (with Project Loom and virtual threads), `ThreadLocal` introduces memory leaks and complex cleanup logic.

Ilmiya utilizes Java 25 **`ScopedValue`** through `TenantContext`:

```kotlin
// Inside TenantContextFilter:
val tenantHeaderValue = request.getHeader("X-Tenant-ID")
if (!tenantHeaderValue.isNullOrBlank()) {
    TenantContext.withTenant(TenantId.of(tenantHeaderValue.trim())) {
        filterChain.doFilter(request, response)
    }
} else {
    filterChain.doFilter(request, response)
}
```

**Benefits:**
- **Zero-Leak Guarantee**: The tenant value is automatically unbound when the enclosing execution scope exits.
- **Virtual Thread Friendly**: Scoped values are immutable and safely inherited across virtual thread boundaries without overhead.
- **Fail-Fast Access**: Calling `TenantContext.requireTenantId()` outside a bound scope throws `MissingTenantContextException` immediately.

---

## Step 4: Fine-Grained Permissions & Role Governance

Ilmiya defines standard permission constants adhering to the `{domain}:{resource}:{action}` notation (`IlmiyaPermissions`):

| Domain | Permission String | Description |
|--------|-------------------|-------------|
| **School** | `school:profile:read` | View institutional profile metadata |
| | `school:profile:write` | Modify school name, address, contact, accreditation |
| | `school:settings:manage` | Configure working days, grading preference, term layout |
| **Academic** | `academic:year:read` | List and view academic years and terms |
| | `academic:year:write` | Create and modify academic years and terms |
| | `academic:curriculum:manage` | Manage curriculum and subject allocations |
| **Student** | `student:profile:read` | View student master records |
| | `student:profile:write` | Register and update student profiles |
| | `student:enrollment:manage` | Manage student admission and class enrollment |
| **Attendance** | `attendance:record:create` | Record daily or period attendance |
| | `attendance:record:approve` | Approve attendance excuses and medical leaves |
| **Finance** | `finance:fee:manage` | Configure fee structures and tuition rates |
| | `finance:payment:create` | Record tuition/fee payments and issue receipts |

### Permission Checks in Controllers
Controllers annotate handler methods with `@RequirePermission`:
```kotlin
@GetMapping("/profile")
@RequirePermission(IlmiyaPermissions.SCHOOL_PROFILE_READ)
fun getProfile(): ResponseEntity<SchoolResponse> { ... }
```

When a user has wildcard permissions (such as `academic:*`), all operations under the `academic` domain are automatically permitted.

---

## Step 5: Multi-Tenant Switching (For Foundation / Yayasan Users)

For multi-institution organizations (e.g., Islamic foundations running primary, secondary, and pesantren units), users with cross-tenant access can switch active tenant contexts:

```bash
curl -s -X POST http://localhost:8080/api/v1/auth/switch-tenant \
  -H "Authorization: Bearer <CURRENT_JWT_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "targetTenantId": "pesantren-darussalam"
  }' | jq
```

**Expected Response (`200 OK`):**
Returns a new JWT access token scoped specifically to `pesantren-darussalam` with tenant-specific roles and permissions.

---

## Troubleshooting

### 401 Unauthorized (`Authentication required`)
- **Reason**: The `Authorization` header is missing, expired, or signed with an unrecognized secret.
- **Remedy**: Refresh token via `/api/v1/auth/refresh` or re-login via `/api/v1/auth/login`. Verify `IAM_JWT_SECRET` in `application.yml`.

### 403 Forbidden (`Access denied`)
- **Reason**: The user is authenticated, but their granted permissions do not satisfy `@RequirePermission`.
- **Remedy**: Inspect the `permissions` array in the JWT payload and grant the necessary permission (or domain wildcard) in `ed-iam`.

### 400 Bad Request (`Missing tenant context`)
- **Reason**: The endpoint requires a tenant boundary, but no `X-Tenant-ID` header was provided.
- **Remedy**: Add `X-Tenant-ID: <tenant-id>` to the request headers.
