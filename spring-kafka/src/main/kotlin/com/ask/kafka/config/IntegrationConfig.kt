package com.ask.kafka.config

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.integration.dsl.IntegrationFlow
import org.springframework.integration.dsl.Transformers
import org.springframework.integration.dsl.integrationFlow
import org.springframework.integration.kafka.dsl.Kafka
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.listener.ConsumerProperties
import org.springframework.kafka.listener.ContainerProperties
import org.springframework.kafka.listener.DefaultErrorHandler

@Configuration
class IntegrationConfig(
  private val consumerFactory: ConsumerFactory<Any, Any>,
  private val defaultErrorHandler: DefaultErrorHandler,
) {
  private val log = LoggerFactory.getLogger(javaClass)

//  @Bean
//  fun kafkaFlow(): IntegrationFlow = integrationFlow(Kafka.inboundChannelAdapter(consumerFactory, consumerProperties())) {
//    handle {
//      log.info("message: {}", it)
//    }
//  }

  private fun consumerProperties(): ConsumerProperties {
    val consumerProperties = ConsumerProperties("sample.topic")
    consumerProperties.clientId = "sample-clientId"
    return consumerProperties
  }

  @Bean
  fun kafkaFlow2(): IntegrationFlow {
    val adapter = Kafka.messageDrivenChannelAdapter(consumerFactory, containerProperties()).configureListenerContainer {
      it.errorHandler(defaultErrorHandler)
      it.groupId((consumerFactory.configurationProperties[ConsumerConfig.GROUP_ID_CONFIG] ?: "kafka-sample") as String)
    }

    return integrationFlow(adapter) {
      transform(Transformers.fromJson())
      handle {
        log.info("message: {}", it)
      }
    }
  }

  private fun containerProperties(): ContainerProperties {
    val containerProperties = ContainerProperties("push.websocket")
    containerProperties.clientId = "sample-clientId"
    return containerProperties
  }

//  @KafkaListener(topics = ["sample.topic"])
//  fun listen(message: String) {
//    log.info("message: {}", message)
//  }
}
