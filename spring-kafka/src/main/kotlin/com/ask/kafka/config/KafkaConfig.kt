package com.ask.kafka.config

import org.apache.kafka.clients.admin.AdminClient
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.DltHandler
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.annotation.RetryableTopic
import org.springframework.kafka.listener.DefaultErrorHandler
import org.springframework.kafka.retrytopic.DltStrategy
import org.springframework.kafka.retrytopic.SameIntervalTopicReuseStrategy
import org.springframework.messaging.MessageHeaders
import org.springframework.messaging.handler.annotation.Headers
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component
import org.springframework.util.backoff.FixedBackOff

@Configuration
class KafkaConfig {
  @Bean
  fun defaultErrorHandler() = DefaultErrorHandler(FixedBackOff(0, 2))

  @Bean
  fun adminClient(kafkaProperties: KafkaProperties): AdminClient {
    return AdminClient.create(kafkaProperties.buildAdminProperties(null))
  }
}

//@Component
class DltHandler {
  private val log = LoggerFactory.getLogger(javaClass)

  @RetryableTopic(
    attempts = "3",
    retryTopicSuffix = ".retry",
    dltTopicSuffix = ".dlt",
    dltStrategy = DltStrategy.FAIL_ON_ERROR,
    sameIntervalTopicReuseStrategy = SameIntervalTopicReuseStrategy.SINGLE_TOPIC // default
  )
  @KafkaListener(topics = ["demo.topic"])
  fun onMessage(
    @Headers headers: MessageHeaders,
    @Payload payload: String,
  ) {
    log.info("header: {}, payload: {}", headers, payload)
    throw IllegalStateException("demo kafka listener exception occurred")
  }

  @DltHandler
  fun processDltMessage(message: String) {
    log.info("dlt handler invoked.. message: {}", message)
  }
}
