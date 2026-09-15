package io.github.edmaputra.ilmiya.app.event

import io.github.edmaputra.ilmiya.academic.domain.port.out.AcademicYearEventPublisher
import io.github.edmaputra.ilmiya.common.domain.DomainEvent
import io.github.edmaputra.ilmiya.core.school.domain.port.out.SchoolEventPublisher
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

/**
 * Spring-backed event publisher adapter routing school events to Spring's [ApplicationEventPublisher].
 *
 * @author edmaputra
 * @since 0.0.1
 */
@Component
class SpringSchoolEventPublisher(
    private val publisher: ApplicationEventPublisher
) : SchoolEventPublisher {

    private val log = LoggerFactory.getLogger(SpringSchoolEventPublisher::class.java)

    override fun publish(event: DomainEvent) {
        log.debug("Publishing school domain event: {}", event::class.simpleName)
        publisher.publishEvent(event)
    }
}

/**
 * Spring-backed event publisher adapter routing academic year events to Spring's [ApplicationEventPublisher].
 *
 * @author edmaputra
 * @since 0.0.1
 */
@Component
class SpringAcademicYearEventPublisher(
    private val publisher: ApplicationEventPublisher
) : AcademicYearEventPublisher {

    private val log = LoggerFactory.getLogger(SpringAcademicYearEventPublisher::class.java)

    override fun publish(event: DomainEvent) {
        log.debug("Publishing academic domain event: {}", event::class.simpleName)
        publisher.publishEvent(event)
    }
}
