# Hexagonal Architecture Rules

This project follows **Hexagonal Architecture (Ports and Adapters)** with **Domain-Driven Design (DDD)** in Kotlin 2.3 and Spring Boot 4.

---

## 1. Multi-Module Layout & Responsibilities

The codebase is structured as a modular monolith where dependencies strictly flow **inward**.

```
Driving Adapters (Web / Controllers / APIs)     Driven Adapters (Persistence / JPA / Clients)
                    \                               /
                     \                             /
                      -->   Domain Model & Ports  <---
                                     ^
                                     |
                       Application Use Cases (Services)
                                     ^
                                     |
                         ilmiya-app (Bootstrap & Wiring)
```

### 1.1 `ilmiya-common` (Shared Kernel & Domain Abstractions)
- **Framework-Agnostic**: Pure Kotlin only. Strictly NO Spring, JPA, Hibernate, or Web dependencies.
- **Contents**:
  - **Domain Primitives & Contracts**: `EntityId<T>`, `TenantId`, `TenantAware`, `AggregateRoot<ID>`.
  - **Domain Events**: `DomainEvent` contract with timestamp and occurrence metadata.
  - **Execution Contexts**: `TenantContext` propagation utilities.
  - **Common DTOs & Envelopes**: `ApiResponse<T>`, `PageResponse<T>`.
  - **Base Domain Exceptions**: `DomainException`, `ResourceNotFoundException`.

### 1.2 Domain Feature Modules (`ilmiya-academic`, `ilmiya-student`, `ilmiya-attendance`, `ilmiya-core`, etc.)
Each domain module contains its own bounded context, divided internally by layer:
- **`domain` (Pure Domain)**:
  - **Entities & Value Objects**: Immutable Kotlin `data class`es enforcing invariants (e.g. `Student`, `Course`).
  - **Inbound Ports**: Single-purpose use-case interfaces (e.g. `AdmitStudentUseCase`, `PromoteStudentUseCase`).
  - **Outbound Ports**: Repository and event publisher interfaces (e.g. `StudentRepository`, `StudentEventPublisher`).
  - **Domain Events & Exceptions**: Specific business state change events and domain errors.
- **`application` (Business Orchestration)**:
  - Application services implementing inbound ports (e.g. `AdmitStudentService : AdmitStudentUseCase`).
  - Pure Kotlin logic with zero Spring framework dependencies (`@Service`, `@Component`, or `@Transactional` are prohibited here).
- **`infrastructure` / `adapter` (Driving & Driven Adapters)**:
  - **Inbound (Web/REST)**: `@RestController` classes, request/response DTOs, OpenAPI annotations.
  - **Outbound (Persistence)**: JPA `@Entity` classes, Spring Data JPA repositories, and repository port implementations.
  - **Isolation Rule**: JPA entities must remain private to the persistence adapter. Always map between JPA entities and domain models.

### 1.3 `ilmiya-app` (Composition Root & Infrastructure)
- **Wiring & Application Entry**: Depends on all feature modules.
- **Contents**:
  - `@SpringBootApplication` main class (`IlmiyaApplication.kt`).
  - Configuration beans and environment-specific settings.
  - Transactional decorators: Wrap core services with Spring's `@Service` and `@Transactional` to manage transaction boundaries without polluting pure use cases.
  - Flyway database migration scripts (`V1__...sql`).

---

## 2. Transaction Management Pattern

Use the **Decorator Pattern** in `ilmiya-app` (or configuration modules) to apply Spring transaction boundaries to pure domain services:

```kotlin
package io.github.edmaputra.ilmiya.app.student

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import io.github.edmaputra.ilmiya.student.application.AdmitStudentService
import io.github.edmaputra.ilmiya.student.domain.Student
import io.github.edmaputra.ilmiya.student.domain.port.`in`.AdmitStudentCommand
import io.github.edmaputra.ilmiya.student.domain.port.`in`.AdmitStudentUseCase
import io.github.edmaputra.ilmiya.student.domain.port.out.StudentEventPublisher
import io.github.edmaputra.ilmiya.student.domain.port.out.StudentRepository
import io.github.edmaputra.ilmiya.common.context.TenantContext

@Service
class TransactionalAdmitStudentUseCase(
    studentRepository: StudentRepository,
    eventPublisher: StudentEventPublisher
) : AdmitStudentUseCase {

    private val delegate = AdmitStudentService(studentRepository, eventPublisher)

    @Transactional
    override fun execute(command: AdmitStudentCommand, context: OperationContext): Student {
        return delegate.execute(command, context)
    }
}
```

---

## 3. Dependency Check List

When adding or modifying code, verify:
- [ ] Does domain code in any module contain Spring or JPA imports? (Must be **NONE**)
- [ ] Does pure application use cases contain Spring `@Service` or `@Transactional` annotations? (Must be **NONE**)
- [ ] Do REST controllers call repositories directly? (Must **ONLY** call `*UseCase` inbound ports)
- [ ] Are JPA entities leaking into domain or REST layers? (Must remain inside persistence adapter packages)
