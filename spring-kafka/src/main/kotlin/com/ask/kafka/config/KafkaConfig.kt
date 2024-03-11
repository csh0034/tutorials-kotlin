package com.ask.kafka.config

import org.springframework.boot.autoconfigure.kafka.DefaultKafkaConsumerFactoryCustomizer
import org.springframework.context.annotation.Bean

//@Configuration
class KafkaConfig {
  @Bean
  fun kafkaConsumerFactoryCustomizer() = DefaultKafkaConsumerFactoryCustomizer {
  }
}
