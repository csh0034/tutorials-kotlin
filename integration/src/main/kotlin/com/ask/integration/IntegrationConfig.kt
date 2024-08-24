package com.ask.integration

import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.integration.dsl.IntegrationFlow
import org.springframework.integration.dsl.integrationFlow
import org.springframework.integration.event.inbound.ApplicationEventListeningMessageProducer
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@Configuration
class IntegrationConfig {
  @Bean
  fun eventsAdapter(): ApplicationEventListeningMessageProducer {
    val producer = ApplicationEventListeningMessageProducer();
    producer.setEventTypes(MessageSaveEvent::class.java)
    return producer
  }

  @Bean
  fun applicationEventFlow(handler: MessageSaveHandler): IntegrationFlow = integrationFlow(eventsAdapter()) {
    handle { handler.handle(it.payload as MessageSaveEvent) }
  }
}

@Component
class MessageSaveHandler {
  private val log = LoggerFactory.getLogger(javaClass)

  fun handle(event: MessageSaveEvent) {
    log.info("{}", event)
  }
}

@RestController
class MessageController(
  private val eventPublisher: ApplicationEventPublisher,
) {
  @GetMapping("/")
  fun send() {
    eventPublisher.publishEvent(MessageSaveEvent(UUID.randomUUID().toString()))
  }
}

data class MessageSaveEvent(
  val messageId: String,
)
