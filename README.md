# Ilmiya

> Multi-tenant School Management System (SMS) and Educational ERP built with Kotlin 2.3, Spring Boot 4.1.2, and Java 25.

---

## Overview

**Ilmiya** is designed to streamline school governance, academic operations, student lifecycle tracking, attendance, examinations, fee management, and human resources for educational institutions (primary, secondary, vocational, and Islamic boarding schools / pesantren).

For the full product vision, target market segments, and roadmap breakdown, see:
- 📖 **[Features and Roadmap](docs/01-features-and-roadmap.md)**
- 🔐 **[Roles and Permissions Specification](docs/02-roles-and-permissions.md)**

---

## Architecture & Technology Stack

- **Language & Runtime**: Kotlin 2.3.21, Java 25 (with `ScopedValue` multi-tenancy context)
- **Framework**: Spring Boot 4.1.2
- **Database & Persistence**: PostgreSQL with Flyway database migrations and Spring Data JPA
- **API Documentation**: OpenAPI 3 with Swagger UI
- **Architecture Pattern**: Hexagonal Architecture (Ports and Adapters) & Domain-Driven Design (DDD)
- **Multi-Tenancy**: Shared database, tenant discriminator column (`tenant_id`), zero-leak thread scoping via `TenantContext`

---

## Module Layout

| Module | Description |
|--------|-------------|
| [`ilmiya-common`](ilmiya-common) | Pure Kotlin shared kernel with domain primitives, base exceptions, and DTOs |
| [`ilmiya-core`](ilmiya-core) | IAM, authentication, RBAC, school configurations, and audit trailing |
| [`ilmiya-academic`](ilmiya-academic) | Academic years, terms, courses, classes/batches, subjects, and promotions |
| [`ilmiya-student`](ilmiya-student) | Student admissions, master records, categories, and guardian linkages |
| [`ilmiya-attendance`](ilmiya-attendance) | Daily/period attendance tracking for students and staff |
| [`ilmiya-timetable`](ilmiya-timetable) | Class schedule timings, teacher allocations, and institutional calendar |
| [`ilmiya-exam`](ilmiya-exam) | Exam sessions, grading scales, marksheets, and report card (e-Rapor) generation |
| [`ilmiya-finance`](ilmiya-finance) | Fee structures, monthly tuition (SPP), invoicing, receipts, and cash journals |
| [`ilmiya-hr`](ilmiya-hr) | Staff management, leave workflows, and school payroll processing |
| [`ilmiya-app`](ilmiya-app) | Spring Boot runner, transactional decorators, and Flyway migrations |

---

## Getting Started

### Prerequisites
- JDK 25
- Docker & Docker Compose (for PostgreSQL, HashiCorp Consul, and HashiCorp Vault)

### Infrastructure & Local Services

By default, Ilmiya connects to **HashiCorp Vault** (for secrets) and **HashiCorp Consul** (for distributed KV configuration and service discovery) out of the box without requiring extra profile flags.

| Workflow | Infrastructure Required | Spring Profile | Description |
|---|---|---|---|
| **Default / Hybrid** (Recommended) | PostgreSQL, Consul, Vault | _(default)_ or `local` | **Default behavior.** Uses Vault for secrets and Consul for configuration & service discovery without flags. |
| **Full Containerized** | Entire stack in Docker | `local` | Runs PostgreSQL, Consul, Vault, initializers, and the Spring Boot application via Docker Compose. |
| **Standalone Minimal** | PostgreSQL only | `local,standalone` | Optional fallback: disables Consul and Vault when running offline without external services. |

#### 1. Start Infrastructure via Docker Compose
```bash
# Recommended: Start database, Consul, and Vault (with automated secrets seeding)
docker compose up -d postgres consul vault consul-vault-init

# (Optional) Launch with pgAdmin UI at http://localhost:5050
docker compose --profile tools up -d
```

> [!NOTE]
> The `consul-vault-init` container automatically seeds the local Vault secrets (`secret/ilmiya-backend`) and Consul KV (`config/ilmiya-backend/data`). If you ever need to re-seed or update them manually, run `./scripts/init-vault-consul.sh`.

#### 2. Service Management UIs
- **Consul Web UI**: [http://localhost:8500/ui/](http://localhost:8500/ui/) (KV store and service discovery catalog)
- **Vault Web UI**: [http://localhost:8200/ui/](http://localhost:8200/ui/) (Token: `root`)
- **pgAdmin**: [http://localhost:5050](http://localhost:5050) (User: `admin@ilmiya.local`, Password: `admin`)

---

### Build & Run Tests
```bash
./gradlew check
```

---

### Run Application

**Option A: Default (Consul and Vault enabled out of the box)**:
```bash
# Run with no arguments (uses default Vault and Consul config):
./gradlew :ilmiya-app:bootRun

# Or explicitly activate the local profile for local test data seeding:
./gradlew :ilmiya-app:bootRun --args='--spring.profiles.active=local'
```

**Option B: Full Containerized Stack**:
```bash
docker compose up -d --build
```

**Option C: Standalone Minimal (Bypass Vault and Consul)**:
```bash
./gradlew :ilmiya-app:bootRun --args='--spring.profiles.active=local,standalone'
```

Once running, access Swagger UI at:
- `http://localhost:8080/swagger-ui.html`
- OpenAPI spec at `http://localhost:8080/api-docs`

---

## Documentation & Standards

- [Features and Roadmap](docs/01-features-and-roadmap.md)
- [Roles and Permissions Specification](docs/02-roles-and-permissions.md)
- [How-To Guides (Operational Runbooks)](docs/how-to/README.md)
- [Agent Guide (AGENTS.md)](AGENTS.md)
- [Clean Code Standards](.agents/rules/java-kotlin/clean-code.md)
- [Hexagonal Architecture Rules](.agents/rules/java-kotlin/hexagonal-architecture.md)
- [Multi-Tenancy & Audit Trail Rules](.agents/rules/java-kotlin/multi-tenancy-and-audit.md)
- [Kotlin Standards](.agents/rules/java-kotlin/kotlin-standards.md)
- [RESTful API Conventions](.agents/rules/java-kotlin/api-conventions.md)
- [Database & Migration Standards](.agents/rules/java-kotlin/database-standards.md)
- [Security Standards](.agents/rules/java-kotlin/security-standards.md)
- [Logging & Observability](.agents/rules/java-kotlin/logging-and-observability.md)
- [Git & CI/CD Standards](.agents/rules/shared/git-and-ci-standards.md)
- [Project Structure & Scanner](.agents/rules/shared/project-structure-standards.md)

