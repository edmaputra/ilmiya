package io.github.edmaputra.ilmiya.app.event

import io.github.edmaputra.ilmiya.common.domain.DomainEvent
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.context.ApplicationEventPublisher
import java.time.Instant

class SpringDomainEventPublishersTest {

    private val applicationEventPublisher = mockk<ApplicationEventPublisher>(relaxed = true)

    private val dummyEvent = object : DomainEvent {
        override val occurredOn: Instant = Instant.now()
    }

    @Test
    fun `SpringSchoolEventPublisher should route event to ApplicationEventPublisher`() {
        val publisher = SpringSchoolEventPublisher(applicationEventPublisher)
        publisher.publish(dummyEvent)

        verify(exactly = 1) { applicationEventPublisher.publishEvent(dummyEvent) }
    }

    @Test
    fun `SpringAcademicYearEventPublisher should route event to ApplicationEventPublisher`() {
        val publisher = SpringAcademicYearEventPublisher(applicationEventPublisher)
        publisher.publish(dummyEvent)

        verify(exactly = 1) { applicationEventPublisher.publishEvent(dummyEvent) }
    }

    @Test
    fun `SpringTenantEventPublisher should route event to ApplicationEventPublisher`() {
        val publisher = SpringTenantEventPublisher(applicationEventPublisher)
        publisher.publish(dummyEvent)

        verify(exactly = 1) { applicationEventPublisher.publishEvent(dummyEvent) }
    }
}
