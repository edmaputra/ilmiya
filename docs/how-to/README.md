# How-To Guides (Operational Runbooks)

This section provides task-oriented, step-by-step operational runbooks for developing with and administering **Ilmiya**. Each guide addresses a specific objective from start to finish.

---

## Guides by Objective

```
docs/how-to/
├── 01-school-registration-and-tenant.md   # Provision and isolate a new school tenant namespace
├── 02-authentication-and-iam.md           # Authenticate with ed-iam, manage permissions & context scoping
├── 03-academic-calendar-and-terms.md      # Configure academic years, terms, and lifecycle transitions
├── 04-school-operational-settings.md      # Set working days, grading preference, and term layout
├── 05-database-and-production-setup.md    # Deploy with PostgreSQL, Flyway, and Java 25 Scoped Values
└── 06-extending-bounded-contexts.md       # Developer guide: Build new DDD modules & hexagonal adapters
```

### 1. Tenant & School Onboarding
- **[01. School Registration & Multi-Tenant Onboarding](01-school-registration-and-tenant.md)**: How to register a new school institution, provision its dedicated tenant boundary (`tenant_id`), configure institutional profile metadata (NPSN, address, accreditation), and verify strict tenant isolation.

### 2. Identity, Access & Multi-Tenancy Scoping
- **[02. Authentication, IAM & Multi-Tenant Context Scoping](02-authentication-and-iam.md)**: How to authenticate using `ed-iam`, formulate authenticated requests using JWT Bearer tokens and the `X-Tenant-ID` header, understand Java 25 `ScopedValue` context propagation, and leverage domain permission wildcards.

### 3. Curriculum & Academic Operations
- **[03. Academic Calendar & Term Lifecycle Configuration](03-academic-calendar-and-terms.md)**: How to define academic years, configure terms/semesters, execute status lifecycle transitions (`PLANNED` $\rightarrow$ `ACTIVE` $\rightarrow$ `ARCHIVED`), and query the active calendar.

### 4. Institutional Configuration
- **[04. Configuring School Operational & Institutional Settings](04-school-operational-settings.md)**: How to customize school working days (5-day or 6-day schedules), configure grading preferences (`NUMERIC_0_100`, `LETTER_A_E`, `PERCENTAGE`, `GPA_4_0`), and select term layouts (`SEMESTER`, `TRIMESTER`, `QUARTER`).

### 5. Infrastructure & Database Operations
- **[05. Database Migrations & Production Deployment](05-database-and-production-setup.md)**: How to spin up PostgreSQL using Docker Compose, execute and baseline Flyway database migrations, configure Java 25 runtime arguments, and inspect Actuator health probes.

### 6. Architecture & Developer Workflows
- **[06. Extending Bounded Contexts (Hexagonal Architecture Runbook)](06-extending-bounded-contexts.md)**: How to implement a new bounded context (e.g. `ilmiya-student`, `ilmiya-finance`) from scratch following Hexagonal Architecture (Ports and Adapters), DDD domain models with zero framework dependencies, JPA adapters, and transactional decorators.

---

## Related Documentation
- [Documentation Portal](../README.md)
- [Executive Features & Roadmap](../01-features-and-roadmap.md)
- [Agent Guide (AGENTS.md)](../../AGENTS.md)
- [Clean Code Standards](../../.agents/rules/java-kotlin/clean-code.md)
- [Hexagonal Architecture Rules](../../.agents/rules/java-kotlin/hexagonal-architecture.md)
- [Multi-Tenancy & Audit Trail Rules](../../.agents/rules/java-kotlin/multi-tenancy-and-audit.md)
