package com.ask.integration

import com.ninjasquad.springmockk.SpykBean
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationEventPublisher

@SpringBootTest
class IntegrationApplicationTest(
  @Autowired private val eventPublisher: ApplicationEventPublisher,
) {
  @SpykBean
  lateinit var messageSaveHandler: MessageSaveHandler
  @Test
  fun `message send`() {
    val event = MessageSaveEvent("mid")
    eventPublisher.publishEvent(event)
    verify(exactly = 1) { messageSaveHandler.handle(event) }
  }
}
