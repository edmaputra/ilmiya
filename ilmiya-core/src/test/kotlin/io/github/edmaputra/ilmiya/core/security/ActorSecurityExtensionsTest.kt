package io.github.edmaputra.ilmiya.core.security

import io.github.edmaputra.iam.domain.context.ActorType
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
    private val fixedUserId = UUID.randomUUID()
    private val fixedTenantId = UUID.randomUUID()

    private val actor = object : CurrentActor {
        override fun userId(): UUID = fixedUserId
        override fun email(): String = "test@school.org"
        override fun tenantId(): UUID = fixedTenantId
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

    @Test
    fun `toOperationContext should map CurrentActor properties correctly`() {
        val defaultCtx = actor.toOperationContext()
        assertThat(defaultCtx.actor()).isEqualTo(actor.userId().toString())
        assertThat(defaultCtx.actorType()).isEqualTo(ActorType.USER)
        assertThat(defaultCtx.optionalTenantUuid()).contains(actor.tenantId())
        assertThat(defaultCtx.optionalCorrelationId()).isEmpty()

        val machineCtx = actor.toOperationContext(correlationId = "corr-123", actorType = ActorType.MACHINE)
        assertThat(machineCtx.actor()).isEqualTo(actor.userId().toString())
        assertThat(machineCtx.actorType()).isEqualTo(ActorType.MACHINE)
        assertThat(machineCtx.optionalTenantUuid()).contains(actor.tenantId())
        assertThat(machineCtx.optionalCorrelationId()).contains("corr-123")
    }

    @Test
    fun `requireOperationContext on CurrentActorProvider should return context or throw`() {
        val provider: CurrentActorProvider = mockk()

        every { provider.currentActor() } returns Optional.of(actor)
        val ctx = provider.requireOperationContext(correlationId = "corr-abc")
        assertThat(ctx.actor()).isEqualTo(actor.userId().toString())
        assertThat(ctx.actorType()).isEqualTo(ActorType.USER)
        assertThat(ctx.optionalCorrelationId()).contains("corr-abc")

        every { provider.currentActor() } returns Optional.empty()
        assertThatThrownBy { provider.requireOperationContext() }
            .isInstanceOf(AccessDeniedException::class.java)
    }

    @Test
    fun `currentOperationContext on CurrentActorProvider should fallback to SYSTEM context`() {
        val provider: CurrentActorProvider = mockk()

        // Case 1: Actor is present
        every { provider.currentActor() } returns Optional.of(actor)
        val actorCtx = provider.currentOperationContext(correlationId = "corr-xyz")
        assertThat(actorCtx.actor()).isEqualTo(actor.userId().toString())
        assertThat(actorCtx.actorType()).isEqualTo(ActorType.USER)
        assertThat(actorCtx.optionalCorrelationId()).contains("corr-xyz")

        // Case 2: Actor is absent, fallback without tenantId
        every { provider.currentActor() } returns Optional.empty()
        val sysCtxWithoutTenant = provider.currentOperationContext()
        assertThat(sysCtxWithoutTenant.actor()).isEqualTo("system")
        assertThat(sysCtxWithoutTenant.actorType()).isEqualTo(ActorType.SYSTEM)
        assertThat(sysCtxWithoutTenant.optionalTenantUuid()).isEmpty()

        // Case 3: Actor is absent, fallback with tenantId
        val fallbackTenant = UUID.randomUUID()
        val sysCtxWithTenant = provider.currentOperationContext(fallbackTenantId = fallbackTenant)
        assertThat(sysCtxWithTenant.actor()).isEqualTo("system")
        assertThat(sysCtxWithTenant.actorType()).isEqualTo(ActorType.SYSTEM)
        assertThat(sysCtxWithTenant.optionalTenantUuid()).contains(fallbackTenant)
    }
}
