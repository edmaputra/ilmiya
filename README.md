# Ilmiya

> Multi-tenant School Management System (SMS) and Educational ERP built with Kotlin 2.3, Spring Boot 4.1.1, and Java 25.

---

## Overview

**Ilmiya** is designed to streamline school governance, academic operations, student lifecycle tracking, attendance, examinations, fee management, and human resources for educational institutions (primary, secondary, vocational, and Islamic boarding schools / pesantren).

For the full product vision, target market segments, and roadmap breakdown, see:
📖 **[Features and Roadmap](docs/01-features-and-roadmap.md)**

---

## Architecture & Technology Stack

- **Language & Runtime**: Kotlin 2.3.21, Java 25 (with `ScopedValue` multi-tenancy context)
- **Framework**: Spring Boot 4.1.1
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
- Docker & Docker Compose (for PostgreSQL)

### Build & Run Tests
```bash
./gradlew check
```

### Run Application
```bash
./gradlew :ilmiya-app:bootRun
```

Once running, access Swagger UI at:
- `http://localhost:8080/swagger-ui.html`
- OpenAPI spec at `http://localhost:8080/api-docs`

---

## Documentation & Standards

- [Features and Roadmap](docs/01-features-and-roadmap.md)
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

