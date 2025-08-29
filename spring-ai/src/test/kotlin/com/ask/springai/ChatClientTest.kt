package com.ask.springai

import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.ai.chat.client.ChatClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class ChatClientTest {
  private val log = LoggerFactory.getLogger(javaClass)

  @Autowired
  lateinit var chatClientBuilder: ChatClient.Builder

  @Test
  fun generation() {
    val chatClient = chatClientBuilder.build()
    val userInput = "llm rag 에 대해서 설명해줘"

    val message = chatClient.prompt()
      .user(userInput)
      .call()
      .content()

    log.info("message: $message")
  }
}
