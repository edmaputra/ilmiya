package io.github.edmaputra.ilmiya.core.security

import io.github.edmaputra.iam.domain.exception.AccessDeniedException
import io.github.edmaputra.iam.domain.security.CurrentActor
import io.github.edmaputra.iam.domain.security.CurrentActorProvider
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatCode
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.util.Optional
import java.util.UUID

class ActorSecurityExtensionsTest {

    private val targetScope = UUID.randomUUID()
    private val otherScope = UUID.randomUUID()

    private val actor = object : CurrentActor {
        override fun userId(): UUID = UUID.randomUUID()
        override fun email(): String = "test@school.org"
        override fun tenantId(): UUID = UUID.randomUUID()
        override fun isPlatformSuperAdmin(): Boolean = false
        override fun isTenantWide(): Boolean = false
        override fun groups(): Set<String> = emptySet()
        override fun roles(): Set<String> = emptySet()
        override fun permissions(): Set<String> = setOf(IlmiyaPermissions.STUDENT_PROFILE_READ, IlmiyaPermissions.ATTENDANCE_READ)
        override fun accessibleScopeNodeIds(): Set<UUID> = setOf(targetScope)
    }

    @Test
    fun `requirePermission should succeed when actor has permission`() {
        assertThatCode { actor.requirePermission(IlmiyaPermissions.STUDENT_PROFILE_READ) }
            .doesNotThrowAnyException()
    }

    @Test
    fun `requirePermission should throw AccessDeniedException when permission is missing`() {
        assertThatThrownBy { actor.requirePermission(IlmiyaPermissions.STUDENT_PROFILE_WRITE) }
            .isInstanceOf(AccessDeniedException::class.java)
            .hasMessageContaining(IlmiyaPermissions.STUDENT_PROFILE_WRITE)
    }

    @Test
    fun `requireAnyPermission and requireAllPermissions should validate correctly`() {
        assertThatCode {
            actor.requireAnyPermission(IlmiyaPermissions.STUDENT_PROFILE_READ, "OTHER_PERM")
        }.doesNotThrowAnyException()

        assertThatThrownBy {
            actor.requireAnyPermission("OTHER_1", "OTHER_2")
        }.isInstanceOf(AccessDeniedException::class.java)

        assertThatCode {
            actor.requireAllPermissions(IlmiyaPermissions.STUDENT_PROFILE_READ, IlmiyaPermissions.ATTENDANCE_READ)
        }.doesNotThrowAnyException()

        assertThatThrownBy {
            actor.requireAllPermissions(IlmiyaPermissions.STUDENT_PROFILE_READ, "MISSING_PERM")
        }.isInstanceOf(AccessDeniedException::class.java)
    }

    @Test
    fun `requireScope should validate scope boundaries`() {
        assertThatCode { actor.requireScope(targetScope) }
            .doesNotThrowAnyException()

        assertThatCode { actor.requireScope(targetScope.toString()) }
            .doesNotThrowAnyException()

        assertThatThrownBy { actor.requireScope(otherScope) }
            .isInstanceOf(AccessDeniedException::class.java)
            .hasMessageContaining(otherScope.toString())

        assertThatThrownBy { actor.requireScope("invalid-uuid-format") }
            .isInstanceOf(AccessDeniedException::class.java)
            .hasMessageContaining("Invalid scope node UUID")
    }

    @Test
    fun `requireActor extension on CurrentActorProvider`() {
        val provider: CurrentActorProvider = mockk()

        every { provider.currentActor() } returns Optional.of(actor)
        assertThat(provider.requireActor()).isEqualTo(actor)

        every { provider.currentActor() } returns Optional.empty()
        assertThatThrownBy { provider.requireActor() }
            .isInstanceOf(AccessDeniedException::class.java)
    }
}
