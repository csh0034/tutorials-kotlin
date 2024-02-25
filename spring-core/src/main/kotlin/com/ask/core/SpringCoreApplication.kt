package com.ask.core

import com.ask.core.config.CoreProperties
import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
@ConfigurationPropertiesScan
class SpringCoreApplication {
  private val log = LoggerFactory.getLogger(javaClass)

  @Bean
  fun loggingRunner(coreProperties: CoreProperties) = ApplicationRunner {
    log.debug("coreProperties: {}", coreProperties)
  }
}

fun main(args: Array<String>) {
  runApplication<SpringCoreApplication>(*args)
}
