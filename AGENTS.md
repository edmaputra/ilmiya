# Agent Guide: ilmiya

Multi-tenant School Management System (SMS) and Educational ERP built with Kotlin 2.3, Spring Boot 4.1.2, and Java 25.

---

## Stack & Versions
- **Language**: Kotlin 2.3 (2.3.21) / Java 25
- **Framework**: Spring Boot 4.x (4.1.2)
- **Persistence**: PostgreSQL with Flyway migrations and Spring Data JPA
- **Build Tool**: Gradle (`./gradlew`)
- **Standards**: Inherited from [`.agents/rules/java-kotlin/`](.agents/rules/java-kotlin/) and [`.agents/rules/shared/`](.agents/rules/shared/)

---

## Architecture: Hexagonal (Ports & Adapters) & DDD
Dependencies flow **strictly inward**: `adapter` ➔ `application` ➔ `domain`.

```
{module}/
├── domain/                      # Core business models, value objects, exceptions, and domain events (zero framework dependencies)
│   ├── model/                  # Domain entities and value objects (Kotlin data classes / Java records)
│   ├── port/
│   │   ├── in/                 # Primary/driving port contracts (Use Cases, Commands)
│   │   └── out/                # Secondary/driven port contracts (SPIs, Repository interfaces)
│   ├── event/                  # Domain event records
│   └── exception/              # Domain-specific exceptions
├── application/                 # Use case implementations and orchestration services (@Service)
│   └── service/                # Use case implementations
├── adapter/                     # Infrastructure and transport adapters
│   ├── in/                     # Driving adapters (Spring Web REST controllers, request/response DTOs)
│   │   └── rest/               # REST controllers, DTOs, mappers
│   └── out/                    # Driven adapters (Spring Data JPA repositories, entity mappers)
│       └── persistence/        # JPA entities, Spring Data repositories, adapter implementations
└── config/                      # Spring Boot beans, SecurityFilterChain, and infrastructure configuration
```

---

## Key Commands
- Build: `./gradlew build`
- Unit & Architecture Tests: `./gradlew check`
- Generate Coverage Report: `./gradlew jacocoAggregateReport`
- Parse Coverage Summary: `python3 .agents/scripts/coverage/generate-jacoco-summary.py`
- Run Application: `./gradlew :ilmiya-app:bootRun`
- Refresh Structure Map: `python3 .agents/scripts/scan-structure.py --force`

---

## Agent Guidelines & Critical Rules
- 🔴 **MUST**: Always inspect [`.agents/project-structure.json`](.agents/project-structure.json) for fast directory and module mapping.
- 🔴 **MUST**: Adhere to coding and architectural rules in `.agents/rules/java-kotlin/` and `.agents/rules/shared/`.
- 🔴 **MUST**: NEVER import Spring, JPA, or Web dependencies inside `domain/`.
- 🔴 **MUST**: Follow Conventional Commits format (`feat`, `fix`, `chore`, etc.) as defined in [`.agents/rules/shared/git-and-ci-standards.md`](.agents/rules/shared/git-and-ci-standards.md).
- 🔴 **MUST**: All domain identifiers must use type-safe `EntityId` / `TenantId` patterns.
- 🔴 **MUST**: Maintain minimum 80% line coverage for domain and application business logic as mandated by CI standards.
- 🔴 **MUST**: Ensure `project-structure.json` is ignored by Git and kept out of commits.
- 🟡 **SHOULD**: Run `./gradlew check jacocoAggregateReport` before concluding code modifications to verify unit tests, ArchUnit rules, and code coverage.

---

## Rule Reference Index
- [Clean Code & Modern Java Standards](.agents/rules/java-kotlin/clean-code.md)
- [Hexagonal Architecture Standards](.agents/rules/java-kotlin/hexagonal-architecture.md)
- [Multi-Tenancy & Audit Trail Standards](.agents/rules/java-kotlin/multi-tenancy-and-audit.md)
- [Kotlin Standards](.agents/rules/java-kotlin/kotlin-standards.md)
- [RESTful API Conventions](.agents/rules/java-kotlin/api-conventions.md)
- [Database & Migration Standards](.agents/rules/java-kotlin/database-standards.md)
- [Security Standards](.agents/rules/java-kotlin/security-standards.md)
- [Logging & Observability Standards](.agents/rules/java-kotlin/logging-and-observability.md)
- [Git & CI/CD Standards](.agents/rules/shared/git-and-ci-standards.md)
- [Project Structure & Agent Discovery Standards](.agents/rules/shared/project-structure-standards.md)
