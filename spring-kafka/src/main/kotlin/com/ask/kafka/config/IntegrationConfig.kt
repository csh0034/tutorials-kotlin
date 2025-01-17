package com.ask.kafka.config

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.integration.dsl.IntegrationFlow
import org.springframework.integration.dsl.Transformers
import org.springframework.integration.dsl.integrationFlow
import org.springframework.integration.kafka.dsl.Kafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory

@Configuration
class IntegrationConfig(
  private val containerFactory: ConcurrentKafkaListenerContainerFactory<Any, Any>,
) {
  private val log = LoggerFactory.getLogger(javaClass)

  @Bean
  fun kafkaFlow(): IntegrationFlow = integrationFlow(kafkaAdaptor()) {
    transform(Transformers.fromJson())
    delay {
      // 예외 발생시 org.springframework.scheduling.support.TaskUtils$LoggingErrorHandler 사용
      messageGroupId("groupId")
      defaultDelay(2000L)
      maxAttempts(1)
    }
    handle {
      log.info("message: {}", it)
      throw IllegalStateException("..")
    }
  }

  private fun kafkaAdaptor() = Kafka.messageDrivenChannelAdapter(containerFactory.createContainer("push.websocket"))
}
