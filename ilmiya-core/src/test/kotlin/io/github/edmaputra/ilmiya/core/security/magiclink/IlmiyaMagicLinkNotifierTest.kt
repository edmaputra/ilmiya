package io.github.edmaputra.ilmiya.core.security.magiclink

import io.github.edmaputra.iam.domain.model.MagicLinkId
import io.github.edmaputra.iam.domain.model.MagicLinkToken
import io.github.edmaputra.iam.domain.model.UserId
import io.github.edmaputra.iam.domain.tenancy.TenantId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.springframework.context.ApplicationEventPublisher
import java.time.Instant
import java.util.UUID

class IlmiyaMagicLinkNotifierTest {

    private lateinit var eventPublisher: ApplicationEventPublisher
    private lateinit var notifier: IlmiyaMagicLinkNotifier

    @BeforeEach
    fun setUp() {
        eventPublisher = mock(ApplicationEventPublisher::class.java)
        notifier = IlmiyaMagicLinkNotifier(eventPublisher)
    }

    @Test
    fun `sendMagicLink should publish MagicLinkDispatchedEvent with tenant UUID`() {
        val tenantUuid = UUID.randomUUID()
        val userId = UserId.generate()
        val now = Instant.now()
        val expiresAt = now.plusSeconds(900)

        val token = MagicLinkToken(
            MagicLinkId.generate(),
            "token-secret-123",
            userId,
            TenantId(tenantUuid),
            "parent@school.sch.id",
            expiresAt,
            null,
            now
        )
        val verificationUrl = "https://portal.school.sch.id/api/v1/auth/magic-link/verify?token=token-secret-123"

        notifier.sendMagicLink(token, verificationUrl)

        val captor = ArgumentCaptor.forClass(MagicLinkDispatchedEvent::class.java)
        verify(eventPublisher).publishEvent(captor.capture())

        val publishedEvent = captor.value
        assertThat(publishedEvent.email).isEqualTo("parent@school.sch.id")
        assertThat(publishedEvent.verificationUrl).isEqualTo(verificationUrl)
        assertThat(publishedEvent.expiresAt).isEqualTo(expiresAt)
        assertThat(publishedEvent.tenantId).isEqualTo(tenantUuid)
    }

    @Test
    fun `sendMagicLink should publish MagicLinkDispatchedEvent when tenant is null`() {
        val userId = UserId.generate()
        val now = Instant.now()
        val expiresAt = now.plusSeconds(900)

        val token = MagicLinkToken(
            MagicLinkId.generate(),
            "token-secret-456",
            userId,
            null,
            "a@example.com",
            expiresAt,
            null,
            now
        )
        val verificationUrl = "https://portal.school.sch.id/api/v1/auth/magic-link/verify?token=token-secret-456"

        notifier.sendMagicLink(token, verificationUrl)

        val captor = ArgumentCaptor.forClass(MagicLinkDispatchedEvent::class.java)
        verify(eventPublisher).publishEvent(captor.capture())

        val publishedEvent = captor.value
        assertThat(publishedEvent.email).isEqualTo("a@example.com")
        assertThat(publishedEvent.verificationUrl).isEqualTo(verificationUrl)
        assertThat(publishedEvent.expiresAt).isEqualTo(expiresAt)
        assertThat(publishedEvent.tenantId).isNull()
    }
}
