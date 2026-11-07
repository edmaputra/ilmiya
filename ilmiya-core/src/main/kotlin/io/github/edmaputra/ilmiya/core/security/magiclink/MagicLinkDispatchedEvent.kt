package io.github.edmaputra.ilmiya.core.security.magiclink

import java.time.Instant
import java.util.UUID

/**
 * Domain event published when a passwordless magic link has been generated and dispatched.
 *
 * Listeners (e.g. SMTP email sender, SMS / WhatsApp notification gateway, or parent portal)
 * can subscribe to this event to deliver the single-click sign-in URL to the recipient.
 *
 * @property email destination email address
 * @property verificationUrl the complete single-click verification URL
 * @property expiresAt timestamp when the magic link token expires
 * @property tenantId target school tenant ID, if any
 * @author edmaputra
 * @since 0.0.1
 */
data class MagicLinkDispatchedEvent(
    val email: String,
    val verificationUrl: String,
    val expiresAt: Instant,
    val tenantId: UUID?
)
