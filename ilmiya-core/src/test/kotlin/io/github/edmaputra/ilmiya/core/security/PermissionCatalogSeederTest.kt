package io.github.edmaputra.ilmiya.core.security

import io.github.edmaputra.iam.domain.model.Permission
import io.github.edmaputra.iam.domain.repository.PermissionRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

class PermissionCatalogSeederTest {

    private lateinit var permissionRepository: PermissionRepository
    private lateinit var seeder: PermissionCatalogSeeder

    @BeforeEach
    fun setUp() {
        permissionRepository = mock(PermissionRepository::class.java)
        seeder = PermissionCatalogSeeder(permissionRepository)
    }

    @Test
    fun `catalog entries should satisfy all validation invariants`() {
        val entries = IlmiyaPermissionCatalog.ENTRIES
        assertThat(entries).isNotEmpty()
        assertThat(IlmiyaPermissionCatalog.BY_CODE).hasSameSizeAs(entries)
        assertThat(IlmiyaPermissionCatalog.CATEGORIES).contains(
            "TENANT", "SCHOOL", "ACADEMIC", "STUDENT", "ATTENDANCE",
            "TIMETABLE", "EXAM", "FINANCE", "HR", "AUDIT"
        )

        entries.forEach { entry ->
            assertThat(entry.code).isNotBlank()
            assertThat(entry.name).isNotBlank()
            assertThat(entry.description).isNotBlank()
            assertThat(entry.category).isNotBlank()
            // Code must follow {domain}:{resource}:{action} or {domain}:*
            assertThat(entry.code).matches("^[a-z]+:([a-z-]+:[a-z-]+|\\*)$")
        }
    }

    @Test
    fun `should seed all permissions when catalog is empty in repository`() {
        // Arrange: no permissions exist yet
        `when`(permissionRepository.existsSystemPermissionByCode(org.mockito.ArgumentMatchers.anyString()))
            .thenReturn(false)

        // Act
        val seededCount = seeder.seedCatalog()

        // Assert
        val totalExpected = IlmiyaPermissionCatalog.ENTRIES.size
        assertThat(seededCount).isEqualTo(totalExpected)

        val captor = ArgumentCaptor.forClass(Permission::class.java)
        verify(permissionRepository, times(totalExpected)).save(captor.capture())

        val savedCodes = captor.allValues.map { it.code() }
        val expectedCodes = IlmiyaPermissionCatalog.ENTRIES.map { it.code }
        assertThat(savedCodes).containsExactlyElementsOf(expectedCodes)
        captor.allValues.forEach { savedPermission ->
            assertThat(savedPermission.systemPermission()).isTrue()
            assertThat(savedPermission.tenantId()).isNull()
        }
    }

    @Test
    fun `should skip seeding when all permissions already exist in repository`() {
        // Arrange: all permissions already exist
        `when`(permissionRepository.existsSystemPermissionByCode(org.mockito.ArgumentMatchers.anyString()))
            .thenReturn(true)

        // Act
        val seededCount = seeder.seedCatalog()

        // Assert
        assertThat(seededCount).isEqualTo(0)
        verify(permissionRepository, never()).save(org.mockito.ArgumentMatchers.any())
    }

    @Test
    fun `should only seed missing permissions in partial seeding scenario`() {
        // Arrange: only first 5 permissions already exist
        val existingCodes = IlmiyaPermissionCatalog.ENTRIES.take(5).map { it.code }.toSet()
        `when`(permissionRepository.existsSystemPermissionByCode(org.mockito.ArgumentMatchers.anyString()))
            .thenAnswer { invocation ->
                val code = invocation.getArgument<String>(0)
                code in existingCodes
            }

        // Act
        val seededCount = seeder.seedCatalog()

        // Assert
        val expectedSeeded = IlmiyaPermissionCatalog.ENTRIES.size - 5
        assertThat(seededCount).isEqualTo(expectedSeeded)

        val captor = ArgumentCaptor.forClass(Permission::class.java)
        verify(permissionRepository, times(expectedSeeded)).save(captor.capture())
        val savedCodes = captor.allValues.map { it.code() }
        assertThat(savedCodes).doesNotContainAnyElementsOf(existingCodes)
    }

    @Test
    fun `run should delegate to seedCatalog`() {
        val appArgs = mock(org.springframework.boot.ApplicationArguments::class.java)
        `when`(permissionRepository.existsSystemPermissionByCode(org.mockito.ArgumentMatchers.anyString()))
            .thenReturn(true)

        seeder.run(appArgs)

        verify(permissionRepository, times(IlmiyaPermissionCatalog.ENTRIES.size))
            .existsSystemPermissionByCode(org.mockito.ArgumentMatchers.anyString())
    }
}
