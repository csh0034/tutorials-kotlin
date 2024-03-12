package com.ask.kafka.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.listener.DefaultErrorHandler
import org.springframework.util.backoff.FixedBackOff

@Configuration
class KafkaConfig {
  @Bean
  fun defaultErrorHandler() = DefaultErrorHandler(FixedBackOff(0, 2))
}
