# How-To: Extending Bounded Contexts (Hexagonal Architecture Runbook)

This guide walks developers through implementing a new bounded context (e.g., `ilmiya-student`, `ilmiya-attendance`, `ilmiya-finance`) in compliance with Ilmiya's strict **Hexagonal Architecture (Ports and Adapters)** and **Domain-Driven Design (DDD)** standards.

---

## Architecture Principles & Critical Rules

Dependencies flow **strictly inward**:

```
Driving Adapters (REST)        Driven Adapters (JPA / Messaging)
        │                                    │
        ▼                                    ▼
Inbound Ports (Use Cases)          Outbound Ports (Repository SPIs)
        │                                    │
        └──────────────► Domain ◄────────────┘
                            ▲
                            │
                   Application Services
```

### Golden Rules
- 🔴 **NEVER** import Spring, Jakarta, JPA, or Web dependencies inside `domain/`.
- 🔴 **ALL** domain identifiers must use typed `EntityId<T>` or `TenantId`.
- 🔴 **ALL** persistent state mutations must respect the multi-tenant discriminator `TenantId`.
- 🔴 **ALL** database write operations must be orchestrated via application services decorated with `@Transactional` in `ilmiya-app`.

---

## Step 1: Establish Module Directory Layout

Create the hexagonal folder structure inside the target module (e.g. `ilmiya-student`):

```
ilmiya-student/src/main/kotlin/io/github/edmaputra/ilmiya/student/
├── domain/                      # PURE Kotlin (Zero framework dependencies)
│   ├── model/                  # Student, StudentId, StudentStatus
│   ├── port/
│   │   ├── in/                 # RegisterStudentUseCase, RegisterStudentCommand
│   │   └── out/                # StudentRepository, StudentEventPublisher
│   ├── event/                  # StudentRegisteredEvent
│   └── exception/              # StudentNotFoundException, DuplicateNisnException
├── application/                 # Use case implementations (@Service)
│   └── service/                # StudentService
├── adapter/                     # Infrastructure & Transport
│   ├── web/                    # StudentController, StudentDtos
│   └── persistence/            # StudentJpaEntity, SpringDataStudentRepository, StudentRepositoryAdapter
```

---

## Step 2: Define Domain Core (Pure Kotlin)

### 1. Type-Safe Entity Identifier
```kotlin
package io.github.edmaputra.ilmiya.student.domain

import io.github.edmaputra.ilmiya.common.domain.EntityId
import java.util.UUID

data class StudentId(override val value: String) : EntityId<String> {
    companion object {
        fun of(value: String): StudentId = StudentId(value)
        fun generate(): StudentId = StudentId(UUID.randomUUID().toString())
    }
}
```

### 2. Aggregate Root
```kotlin
package io.github.edmaputra.ilmiya.student.domain

import io.github.edmaputra.ilmiya.common.domain.AggregateRoot
import io.github.edmaputra.ilmiya.common.domain.TenantAware
import io.github.edmaputra.ilmiya.common.domain.TenantId
import java.time.Instant
import java.time.LocalDate

class Student(
    override val id: StudentId,
    override val tenantId: TenantId,
    var fullName: String,
    val nisn: String,
    var dateOfBirth: LocalDate,
    var status: StudentStatus = StudentStatus.ACTIVE,
    val createdAt: Instant = Instant.now(),
    var updatedAt: Instant = Instant.now()
) : AggregateRoot<StudentId>(), TenantAware {

    fun updateProfile(fullName: String, dateOfBirth: LocalDate) {
        this.fullName = fullName
        this.dateOfBirth = dateOfBirth
        this.updatedAt = Instant.now()
    }
}
```

### 3. Ports (Inbound & Outbound Contracts)

**Inbound Port (`domain/port/in/RegisterStudentUseCase.kt`):**
```kotlin
package io.github.edmaputra.ilmiya.student.domain.port.`in`

import io.github.edmaputra.ilmiya.common.domain.TenantId
import io.github.edmaputra.ilmiya.student.domain.Student

interface RegisterStudentUseCase {
    fun register(tenantId: TenantId, command: RegisterStudentCommand): Student
}
```

**Outbound Port (`domain/port/out/StudentRepository.kt`):**
```kotlin
package io.github.edmaputra.ilmiya.student.domain.port.out

import io.github.edmaputra.ilmiya.common.domain.TenantId
import io.github.edmaputra.ilmiya.student.domain.Student
import io.github.edmaputra.ilmiya.student.domain.StudentId

interface StudentRepository {
    fun save(student: Student): Student
    fun findById(tenantId: TenantId, id: StudentId): Student?
    fun existsByNisn(tenantId: TenantId, nisn: String): Boolean
}
```

---

## Step 3: Implement Application Service

The application service orchestrates business rules and outbound ports without framework-specific transport bindings:

```kotlin
package io.github.edmaputra.ilmiya.student.application

import io.github.edmaputra.ilmiya.common.domain.TenantId
import io.github.edmaputra.ilmiya.student.domain.Student
import io.github.edmaputra.ilmiya.student.domain.StudentId
import io.github.edmaputra.ilmiya.student.domain.exception.DuplicateNisnException
import io.github.edmaputra.ilmiya.student.domain.port.`in`.RegisterStudentCommand
import io.github.edmaputra.ilmiya.student.domain.port.`in`.RegisterStudentUseCase
import io.github.edmaputra.ilmiya.student.domain.port.out.StudentRepository
import org.springframework.stereotype.Service

@Service
class StudentService(
    private val studentRepository: StudentRepository
) : RegisterStudentUseCase {

    override fun register(tenantId: TenantId, command: RegisterStudentCommand): Student {
        if (studentRepository.existsByNisn(tenantId, command.nisn)) {
            throw DuplicateNisnException("Student with NISN ${command.nisn} already exists for tenant ${tenantId.value}")
        }

        val student = Student(
            id = StudentId.generate(),
            tenantId = tenantId,
            fullName = command.fullName,
            nisn = command.nisn,
            dateOfBirth = command.dateOfBirth
        )

        return studentRepository.save(student)
    }
}
```

---

## Step 4: Implement Driven Adapter (Persistence)

Map domain models to Spring Data JPA entities in `adapter/persistence/`:

```kotlin
@Entity
@Table(name = "student_records")
class StudentJpaEntity(
    @Id
    val id: String,
    @Column(name = "tenant_id", nullable = false)
    val tenantId: String,
    @Column(name = "full_name", nullable = false)
    var fullName: String,
    @Column(name = "nisn", nullable = false)
    val nisn: String,
    @Column(name = "date_of_birth", nullable = false)
    var dateOfBirth: LocalDate,
    @Enumerated(EnumType.STRING)
    var status: StudentStatus
)
```

Implement `StudentRepository` port via `StudentRepositoryAdapter`:
```kotlin
@Component
class StudentRepositoryAdapter(
    private val springDataRepository: SpringDataStudentRepository
) : StudentRepository {

    override fun save(student: Student): Student {
        val jpaEntity = student.toJpa()
        val saved = springDataRepository.save(jpaEntity)
        return saved.toDomain()
    }

    override fun findById(tenantId: TenantId, id: StudentId): Student? =
        springDataRepository.findByTenantIdAndId(tenantId.value, id.value)?.toDomain()

    override fun existsByNisn(tenantId: TenantId, nisn: String): Boolean =
        springDataRepository.existsByTenantIdAndNisn(tenantId.value, nisn)
}
```

---

## Step 5: Implement Driving Adapter (Web Controller)

Expose REST endpoints in `adapter/web/`:

```kotlin
@RestController
@RequestMapping("/api/v1/students")
class StudentController(
    private val registerStudentUseCase: RegisterStudentUseCase
) {

    @PostMapping
    @RequirePermission(IlmiyaPermissions.STUDENT_PROFILE_WRITE)
    fun registerStudent(
        @Valid @RequestBody request: RegisterStudentRequest
    ): ResponseEntity<StudentResponse> {
        val tenantId = TenantContext.requireTenantId()
        val student = registerStudentUseCase.register(tenantId, request.toCommand())
        val location = URI.create("/api/v1/students/${student.id.value}")
        return ResponseEntity.created(location).body(StudentResponse.from(student))
    }
}
```

---

## Step 6: Register Transactional Decorator in `ilmiya-app`

Following clean architecture, transactions are decorated at the composition root (`ilmiya-app`) rather than inside domain application modules:

```kotlin
package io.github.edmaputra.ilmiya.app.student

import io.github.edmaputra.ilmiya.common.domain.TenantId
import io.github.edmaputra.ilmiya.student.application.StudentService
import io.github.edmaputra.ilmiya.student.domain.Student
import io.github.edmaputra.ilmiya.student.domain.port.`in`.RegisterStudentCommand
import io.github.edmaputra.ilmiya.student.domain.port.`in`.RegisterStudentUseCase
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Primary
class TransactionalStudentUseCase(
    private val studentService: StudentService
) : RegisterStudentUseCase {

    @Transactional
    override fun register(tenantId: TenantId, command: RegisterStudentCommand): Student =
        studentService.register(tenantId, command)
}
```

---

## Step 7: Verify with ArchUnit

Verify architectural purity by running the automated check suite:

```bash
./gradlew check
```

ArchUnit will verify that:
1. No Spring or JPA classes leak into `domain/`.
2. Packages adhere strictly to onion/hexagonal dependency directions.
3. Controller layer calls use-case ports, not repository ports directly.
