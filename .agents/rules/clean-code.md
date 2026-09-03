# Clean Code & Modern Kotlin Standards

This document establishes clean code practices and coding standards for this repository (Kotlin 2.3 / Java 25 / Spring Boot 4).

---

## 1. Immutability & Kotlin Data Classes

- **Default to Kotlin `data class`**: Use `data class` for all Domain Entities, Value Objects, Commands, Events, and DTOs.
- **Fail-Fast with `init` Invariant Checks**: Validate invariants directly inside the `init` block using Kotlin's idiomatic `require(...)` or `check(...)` to prevent instantiating invalid objects.
- **Immutable & Read-Only Collections**: Return read-only collections (`List<T>`, `Set<T>`, `Map<K, V>`) instead of mutable collections (`MutableList<T>`) to prevent internal state mutations from leaking.

```kotlin
// ✅ GOOD: Immutable data class with fail-fast init invariant checks
data class CreateStudentCommand(
    val firstName: String,
    val lastName: String,
    val admissionNumber: String
) {
    init {
        require(firstName.isNotBlank()) { "Student first name must not be blank." }
        require(lastName.isNotBlank()) { "Student last name must not be blank." }
        require(admissionNumber.isNotBlank()) { "Student admission number must not be blank." }
    }
}

// ❌ BAD: Mutable class with vars, getters/setters, and deferred validation
class CreateStudentCommand {
    var firstName: String? = null
    var lastName: String? = null
    var admissionNumber: String? = null
}
```

---

## 2. Interface Segregation & Single-Purpose Ports

- **One Inbound Port = One Intention**: Avoid monolithic "god" service interfaces. Create single-purpose use case interfaces (`CreateStudentUseCase`, `PromoteStudentUseCase`).
- **Encapsulate Arguments in Commands**: If a use case requires multiple inputs, encapsulate them into a strongly typed `*Command` data class rather than passing long parameter lists.

```kotlin
// ✅ GOOD: Focused, single-purpose Inbound Port
interface ConfigureSchoolSettingsUseCase {
    fun execute(command: ConfigureSchoolSettingsCommand, context: OperationContext): SchoolSetting
}

// ❌ BAD: Monolithic service interface with dozens of unrelated methods
interface StudentService {
    fun create(name: String): Student
    fun update(id: String, name: String)
    fun configure(key: String, value: String): SchoolSetting
    fun delete(id: String)
    fun listAll(): List<Student>
}
```

---

## 3. Explicit Dependency Injection & Zero Magic

- **Primary Constructor Injection with `val`**: Always inject dependencies via primary constructor properties.
- **No Lombok**: Kotlin's built-in primary constructors and properties eliminate the need for Lombok entirely.
- **Field Injection Strictly Prohibited**: Never use `@Autowired` on private or `lateinit var` properties.
- **No Spring in Domain/Core**: Never import `@Service`, `@Component`, `@Autowired`, or `@Value` inside pure domain packages or core application logic.

```kotlin
// ✅ GOOD: Explicit dependencies via primary constructor, pure Kotlin
class CreateStudentService(
    private val studentRepository: StudentRepository,
    private val eventPublisher: StudentEventPublisher
) : CreateStudentUseCase {
    override fun execute(command: CreateStudentCommand, context: OperationContext): Student {
        // ...
    }
}

// ❌ BAD: Spring field injection inside core logic
class CreateStudentService {
    @Autowired
    private lateinit var studentRepository: StudentRepository
}
```

---

## 4. Null Safety & Clean Error Handling

- **Leverage Kotlin Null Safety**:
  - Use Kotlin's first-class nullable types (`T?`) and safe-call operators (`?.`, `?:`, `let`, `run`) rather than Java `Optional<T>` in pure Kotlin code.
  - Return empty collections (`emptyList()`, `emptySet()`) instead of `null` for collection queries.
- **Domain Exceptions**:
  - Throw specific domain exceptions (e.g. `StudentNotFoundException`, `DuplicateAdmissionNumberException`) extending `DomainException` instead of throwing generic `RuntimeException` or returning error codes.
  - Domain exceptions must reside in the relevant domain package under `exception`.
- **Centralized REST Mapping**:
  - Catch domain exceptions in the REST adapter layer via `@ExceptionHandler` inside `@RestControllerAdvice`. Translate them to standard HTTP status codes and RFC 7807 problem responses.

```kotlin
// ✅ GOOD: Idiomatic Kotlin null check and domain exception
fun findOrThrow(admissionNumber: String): Student =
    studentRepository.findByAdmissionNumber(admissionNumber)
        ?: throw StudentNotFoundException("Student with admission number '$admissionNumber' was not found")

// ❌ BAD: Returning null, nullable checks without handling, and generic exception
fun find(admissionNumber: String): Student? {
    val student = studentRepository.findByAdmissionNumber(admissionNumber)
    if (student == null) {
        throw RuntimeException("Duplicate admission number")
    }
    return student
}
```

---

## 5. Guard Clauses & Flat Code

- **Guard Clauses**: Validate preconditions early and exit/throw immediately using `require(...)`, `check(...)`, or early returns to avoid nested `if-else` blocks (arrow anti-pattern).
- **Pattern Matching & Expressions**: Prefer Kotlin `when` expressions over chained condition ladders.

```kotlin
// ✅ GOOD: Flat guard clauses
fun validateSetting(setting: SchoolSetting) {
    require(setting.key.isNotBlank()) { "Setting key must not be blank." }
    require(setting.value.isNotBlank()) { "Setting value must not be blank." }
    // Happy path proceeds with no indentation
}

// ❌ BAD: Deeply nested indentation
fun validateSetting(setting: SchoolSetting?) {
    if (setting != null) {
        if (setting.key.isNotBlank()) {
            // Happy path buried deep
        } else {
            throw InvalidSettingException("Setting key must not be blank.")
        }
    } else {
        throw IllegalArgumentException("Setting must not be null.")
    }
}
```

---

## 6. Naming & Language Conventions

- **English Only**: Use standard English naming for all classes, methods, properties, database tables, and REST endpoints.
- **Intention-Revealing Names**:
  - Use Cases: Verb phrases (e.g., `CreateStudentUseCase`, `PromoteStudentUseCase`).
  - Domain Models: Noun phrases (e.g., `Student`, `TenantId`, `AuditEntry`).
  - Outbound Ports: Repository or Publisher nouns (e.g., `StudentRepository`, `StudentEventPublisher`).
  - Controllers: Resource nouns (e.g., `StudentManagementController`).
- **REST Endpoints**: Lowercase, plural kebab-case (e.g., `/api/v1/students`, `/api/v1/school-settings`).

---

## 7. Testing Standards

- **Sub-Second Unit Tests**: Pure unit tests in domain and use-case layers without starting Spring Boot (`@SpringBootTest`). Use mock repositories or in-memory fakes using **MockK** (`io.mockk:mockk`).
- **Integration & Slices**: Keep full Spring Boot integration tests (`@SpringBootTest`, Testcontainers) in `ilmiya-app` or adapter slice tests with `@WebMvcTest` and `SpringMockK` (`com.ninja-squad:springmockk`).
- **Expressive Test Names**: Use backtick syntax for readable test names describing behaviors:
  ```kotlin
  @Test
  fun `should throw exception when student admission number already exists`() {
      // Arrange, Act, Assert
  }
  ```

---

## 8. JSON Serialization Standards (Jackson 3.x / `tools.jackson.*`)

- **Jackson 3 Namespace Requirement**: Always use `tools.jackson.*` package imports for JSON serialization and parsing in all modern modules. Never use obsolete `com.fasterxml.jackson.*` packages directly unless binding to third-party APIs.
- **Maven Coordinates**: Depend on `tools.jackson.module:jackson-module-kotlin` and `tools.jackson.core:jackson-databind`. Modern Java and Kotlin features (`java.time.*`, `data class`es, nullability) are supported natively.
- **Builder Construction & Immutability**:
  - Instantiate standalone mappers using `JsonMapper.builder()`:
  ```kotlin
  // ✅ GOOD: Jackson 3 builder pattern
  import tools.jackson.databind.ObjectMapper
  import tools.jackson.databind.json.JsonMapper

  val objectMapper: ObjectMapper = JsonMapper.builder().build()
  ```

---

## 9. Comprehensive KDoc Documentation Standards

- **Mandatory Class, Interface, Data Class, and Enum KDoc**:
  - Every Kotlin class, interface, `data class`, and `enum` MUST have a descriptive KDoc block summarizing its purpose, domain context, and architectural role (Inbound Port, Outbound Port, Domain Entity, Adapter, etc.).
  - **`@author` Tag Requirement**: Every type-level KDoc must include the `@author` tag (e.g. `@author edmaputra`).
- **Public & Protected Methods Documentation**:
  - All public and protected functions across interfaces, ports, domain services, and adapters must be documented with clear descriptions, `@param` for inputs, `@return` for results, and `@throws` for expected domain/runtime exceptions.
- **Data Class Invariants**:
  - Document domain invariant constraints, validation rules, and structural behaviors.
- **Concise & Meaningful**:
  - Avoid empty or tautological comments (e.g. `/** Gets the student. */`). Provide real context on intent, behavior, and lifecycle.

```kotlin
// ✅ GOOD: Meaningful class and method KDoc with @author tag
/**
 * Inbound port for managing student admission lifecycle and initial enrollment.
 * Coordinates validation across registered academic departments and generates unique student profiles.
 *
 * @author edmaputra
 */
interface AdmitStudentUseCase {

    /**
     * Enrolls a student candidate and issues an official institutional profile.
     *
     * @param command the admission command containing student demographics and target course
     * @param context the operational multi-tenant context
     * @return the newly admitted student aggregate
     * @throws DuplicateAdmissionNumberException if the admission number is already allocated
     * @throws CourseNotFoundException if the target course does not exist
     */
    fun execute(command: AdmitStudentCommand, context: OperationContext): Student
}
```
