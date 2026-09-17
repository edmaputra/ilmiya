# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog (v1.1.0)](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning (v2.0.0)](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Root `AGENTS.md` AI agent guide conforming to `ed-agent-standards` multi-module Hexagonal Architecture template.
- Outbound persistence and event publishing ports (`SchoolRepository`, `SchoolEventPublisher`, `AcademicYearRepository`, `AcademicYearEventPublisher`).
- Phase 1 School Profile and Academic Year management:
  - Institutional profiles with multi-tenant isolation, accreditation details, operational schedules, and international registration types.
  - Academic calendar configuration with academic years, terms/semesters, and activation workflows.
  - Multi-tenant query scoping and transactional use case boundaries.
- Spring Security and IAM integration via `ed-iam` starter library:
  - Role-based and permission-based access control annotations (`@RequirePermission`).
  - Scoped value actor propagation and MDC tenant correlation via `IlmiyaTenantContextBridge`.
  - RFC 9457 `ProblemDetail` security exception mapping in `IlmiyaSecurityExceptionHandler`.
- Hexagonal architecture boundary enforcement with ArchUnit test suite.

### Changed
- Updated `ed-agent-standards` submodule to latest commit with multi-stack structure (`rules/java-kotlin/`, `rules/shared/`) and automated structure scanner.
- Updated `README.md` documentation links reflecting the stack-based rule structure.
- Upgraded `ed-iam` starter library to `0.2.0`, integrating enhanced `OperationContext` and `ActorType` (`USER`, `SYSTEM`, `MACHINE`) extensions for actor scoping and auditing.
- Migrated codebase to idiomatic Kotlin targeting Java 25 and Spring Boot 4.1.1.
- REST endpoints now return direct domain DTOs without wrapper envelopes, aligning with API conventions.
- Global exception handling refactored to emit RFC 9457 `ProblemDetail` payloads.
- Database schema and JPA entities standardized with `{module}_*` table names (`core_schools`, `academic_years`), `@Version` optimistic locking, and actor audit columns (`created_by`, `updated_by`).

### Fixed
- Scoped IDE `/out/` and `/bin/` in `.gitignore` to root level to prevent accidental exclusion of source packages like `port/out/`.

### Security
- Stateless JWT authentication filter pipeline with deny-by-default endpoint authorization.
- Multi-tier defense-in-depth authorization with transport, method, domain, and data scoping.

[Unreleased]: https://github.com/edmaputra/ilmiya/compare/v0.0.1...HEAD
