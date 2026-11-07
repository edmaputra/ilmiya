package io.github.edmaputra.ilmiya.core.security.magiclink

import io.github.edmaputra.iam.application.port.out.MagicLinkNotifierPort
import io.github.edmaputra.iam.domain.model.MagicLinkToken
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

/**
 * Educational ERP implementation of [MagicLinkNotifierPort].
 *
 * Intercepts passwordless magic link dispatch events from ed-iam, logs the action
 * for audit purposes, and publishes a [MagicLinkDispatchedEvent] on the Spring event bus.
 *
 * @author edmaputra
 * @since 0.0.1
 */
@Component
class IlmiyaMagicLinkNotifier(
    private val eventPublisher: ApplicationEventPublisher
) : MagicLinkNotifierPort {

    private val log = LoggerFactory.getLogger(IlmiyaMagicLinkNotifier::class.java)

    override fun sendMagicLink(magicLinkToken: MagicLinkToken, verificationUrl: String) {
        val tenantUuid = magicLinkToken.tenantId()?.value()
        val maskedEmail = maskEmail(magicLinkToken.email())

        log.info(
            "Dispatched magic link sign-in to user [email='{}', tenant='{}'] with expiration at {}",
            maskedEmail,
            tenantUuid ?: "GLOBAL",
            magicLinkToken.expiresAt()
        )
        log.debug("Verification URL for [email='{}']: {}", magicLinkToken.email(), verificationUrl)

        val event = MagicLinkDispatchedEvent(
            email = magicLinkToken.email(),
            verificationUrl = verificationUrl,
            expiresAt = magicLinkToken.expiresAt(),
            tenantId = tenantUuid
        )
        eventPublisher.publishEvent(event)
    }

    private fun maskEmail(email: String): String {
        val atIndex = email.indexOf('@')
        if (atIndex <= 1) return "***" + email.substring(atIndex)
        val username = email.substring(0, atIndex)
        val domain = email.substring(atIndex)
        val maskedUser = username.first() + "***" + username.last()
        return maskedUser + domain
    }
}
