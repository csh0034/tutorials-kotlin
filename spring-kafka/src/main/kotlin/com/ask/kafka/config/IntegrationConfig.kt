package com.ask.kafka.config

import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.header.internals.RecordHeaders
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.NestedExceptionUtils
import org.springframework.integration.dsl.IntegrationFlow
import org.springframework.integration.dsl.Transformers
import org.springframework.integration.dsl.integrationFlow
import org.springframework.integration.kafka.dsl.Kafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.KafkaOperations
import org.springframework.kafka.listener.CommonErrorHandler
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer
import org.springframework.kafka.listener.DefaultErrorHandler
import org.springframework.util.backoff.FixedBackOff

//@Configuration
class IntegrationConfig(
  private val containerFactory: ConcurrentKafkaListenerContainerFactory<Any, Any>,
  private val kafkaTemplate: KafkaOperations<Any, Any>,
) {
  private val log = LoggerFactory.getLogger(javaClass)

  //  @Bean
  fun delayFlow(): IntegrationFlow = integrationFlow(kafkaAdaptor(KafkaTopic.DEMO1)) {
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

  @Bean
  fun demoFlow(): IntegrationFlow = integrationFlow(kafkaAdaptor(KafkaTopic.DEMO1)) {
    handle {
      log.info("demo flow invoked..")
      throw IllegalStateException("demo flow exception occurred")
    }
  }

  @Bean
  fun dltFlow(): IntegrationFlow = integrationFlow(kafkaAdaptor(KafkaTopic.DLT)) {
    handle {
      log.info("dlt flow invoked.. message: {}", it)
    }
  }

  private fun kafkaAdaptor(kafkaTopic: KafkaTopic) = Kafka.messageDrivenChannelAdapter(
    containerFactory.createContainer(kafkaTopic.topicName).apply {
      if (kafkaTopic.dlt) commonErrorHandler = createDltErrorHandler()
    }
  )

  private fun createDltErrorHandler(): CommonErrorHandler {
    val recoverer = DeadLetterPublishingRecoverer(kafkaTemplate) { _, _ ->
      TopicPartition(KafkaTopic.DLT.topicName, -1)
    }.apply {
      initializeHeader()
      includeCustomHeader()
      includeHeader(DeadLetterPublishingRecoverer.HeaderNames.HeadersToAdd.GROUP)
      includeHeader(DeadLetterPublishingRecoverer.HeaderNames.HeadersToAdd.TOPIC)
    }

    val backOff = FixedBackOff(0, 0)
    return DefaultErrorHandler(recoverer, backOff)
  }

  private fun DeadLetterPublishingRecoverer.initializeHeader() {
    excludeHeader(*DeadLetterPublishingRecoverer.HeaderNames.HeadersToAdd.entries.toTypedArray())
  }

  private fun DeadLetterPublishingRecoverer.includeCustomHeader() {
    addHeadersFunction { _, ex ->
      val rootCause = NestedExceptionUtils.getMostSpecificCause(ex)
      RecordHeaders().apply {
        add("error-class-fqcn", rootCause.javaClass.name.toByteArray())
        add("error-message", rootCause.message?.toByteArray())
      }
    }
  }
}

enum class KafkaTopic(val topicName: String, val dlt: Boolean) {
  DEMO1("demo.topic", true),
  DLT("dlt", false),
}
