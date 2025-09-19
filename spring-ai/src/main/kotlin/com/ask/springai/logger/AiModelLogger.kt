package com.ask.springai.logger

import org.slf4j.LoggerFactory
import org.springframework.ai.model.SpringAIModelProperties
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component

@Component
class AiModelLogger(
  private val environment: Environment,
): ApplicationRunner {
  private val log = LoggerFactory.getLogger(javaClass)

  override fun run(args: ApplicationArguments?) {
    log.info("chat model: {}", environment.getProperty(SpringAIModelProperties.CHAT_MODEL))
    log.info("embedding model: {}", environment.getProperty(SpringAIModelProperties.EMBEDDING_MODEL))
  }
}
