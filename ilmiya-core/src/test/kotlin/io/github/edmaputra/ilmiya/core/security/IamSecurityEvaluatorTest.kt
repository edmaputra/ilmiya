package io.github.edmaputra.ilmiya.core.security

import io.github.edmaputra.iam.adapter.security.evaluator.IamSecurityEvaluator
import io.github.edmaputra.iam.domain.security.CurrentActor
import io.github.edmaputra.iam.domain.security.CurrentActorProvider
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.Optional
import java.util.UUID

class IamSecurityEvaluatorTest {

    private val currentActorProvider: CurrentActorProvider = mockk()
    private lateinit var evaluator: IamSecurityEvaluator

    private val tenantId = UUID.randomUUID()
    private val userId = UUID.randomUUID()
    private val scope1 = UUID.randomUUID()
    private val scope2 = UUID.randomUUID()

    @BeforeEach
    fun setUp() {
        evaluator = IamSecurityEvaluator(currentActorProvider)
    }

    @Test
    fun `should evaluate permissions correctly for standard user`() {
        val actor = object : CurrentActor {
            override fun userId(): UUID = this@IamSecurityEvaluatorTest.userId
            override fun email(): String = "teacher@school.org"
            override fun tenantId(): UUID = this@IamSecurityEvaluatorTest.tenantId
            override fun isPlatformSuperAdmin(): Boolean = false
            override fun isTenantWide(): Boolean = false
            override fun groups(): Set<String> = setOf("TEACHERS")
            override fun roles(): Set<String> = setOf("TEACHER")
            override fun permissions(): Set<String> = setOf(IlmiyaPermissions.ATTENDANCE_RECORD, IlmiyaPermissions.GRADE_ENTRY)
            override fun accessibleScopeNodeIds(): Set<UUID> = setOf(scope1)
        }

        every { currentActorProvider.currentActor() } returns Optional.of(actor)

        assertThat(evaluator.hasPermission(IlmiyaPermissions.ATTENDANCE_RECORD)).isTrue()
        assertThat(evaluator.hasPermission(IlmiyaPermissions.STUDENT_PROFILE_WRITE)).isFalse()

        assertThat(evaluator.hasAnyPermission(IlmiyaPermissions.ATTENDANCE_RECORD, IlmiyaPermissions.STUDENT_PROFILE_WRITE)).isTrue()
        assertThat(evaluator.hasAnyPermission("UNKNOWN_1", "UNKNOWN_2")).isFalse()

        assertThat(evaluator.hasAllPermissions(IlmiyaPermissions.ATTENDANCE_RECORD, IlmiyaPermissions.GRADE_ENTRY)).isTrue()
        assertThat(evaluator.hasAllPermissions(IlmiyaPermissions.ATTENDANCE_RECORD, IlmiyaPermissions.STUDENT_PROFILE_WRITE)).isFalse()

        assertThat(evaluator.canAccessScope(scope1)).isTrue()
        assertThat(evaluator.canAccessScope(scope2)).isFalse()

        assertThat(evaluator.hasRole("TEACHER")).isTrue()
        assertThat(evaluator.hasRole("STUDENT")).isFalse()

        assertThat(evaluator.isInGroup("TEACHERS")).isTrue()
        assertThat(evaluator.isInGroup("STUDENTS")).isFalse()

        assertThat(evaluator.isPlatformSuperAdmin()).isFalse()
        assertThat(evaluator.isTenantWide()).isFalse()
    }

    @Test
    fun `should allow superadmin full permission and scope access`() {
        val superAdminActor = object : CurrentActor {
            override fun userId(): UUID = this@IamSecurityEvaluatorTest.userId
            override fun email(): String = "admin@platform.com"
            override fun tenantId(): UUID? = null
            override fun isPlatformSuperAdmin(): Boolean = true
            override fun isTenantWide(): Boolean = true
            override fun groups(): Set<String> = emptySet()
            override fun roles(): Set<String> = emptySet()
            override fun permissions(): Set<String> = emptySet()
            override fun accessibleScopeNodeIds(): Set<UUID> = emptySet()
        }

        every { currentActorProvider.currentActor() } returns Optional.of(superAdminActor)

        assertThat(evaluator.hasPermission("ANY_PERMISSION_NOT_LISTED")).isTrue()
        assertThat(evaluator.canAccessScope(scope1)).isTrue()
        assertThat(evaluator.canAccessScope(scope2)).isTrue()
        assertThat(evaluator.isPlatformSuperAdmin()).isTrue()
    }

    @Test
    fun `should return false when unauthenticated`() {
        every { currentActorProvider.currentActor() } returns Optional.empty()

        assertThat(evaluator.hasPermission(IlmiyaPermissions.ATTENDANCE_RECORD)).isFalse()
        assertThat(evaluator.hasAnyPermission(IlmiyaPermissions.ATTENDANCE_RECORD)).isFalse()
        assertThat(evaluator.hasAllPermissions(IlmiyaPermissions.ATTENDANCE_RECORD)).isFalse()
        assertThat(evaluator.canAccessScope(scope1)).isFalse()
        assertThat(evaluator.hasRole("TEACHER")).isFalse()
        assertThat(evaluator.isInGroup("TEACHERS")).isFalse()
        assertThat(evaluator.isPlatformSuperAdmin()).isFalse()
        assertThat(evaluator.isTenantWide()).isFalse()
    }
}
