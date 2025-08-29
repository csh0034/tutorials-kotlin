package com.ask.springai

import org.apache.coyote.http11.Constants.a
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor
import org.springframework.ai.chat.memory.ChatMemory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class ChatClientTest {
  private val log = LoggerFactory.getLogger(javaClass)

  @Autowired
  lateinit var chatClientBuilder: ChatClient.Builder

  @Autowired
  lateinit var chatMemory: ChatMemory

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

  @Test
  fun withAdvisor() {
    val advisor = PromptChatMemoryAdvisor.builder(chatMemory)
      .build()

    val chatClient = chatClientBuilder
      .defaultAdvisors(advisor)
      .build()

    val userInput = "llm rag 에 대해서 설명해줘"

    val message = chatClient.prompt()
      .system("영어로 대답해줘")
      .user(userInput)
      .advisors { a -> a.param(ChatMemory.CONVERSATION_ID, "cid") }
      .call()
      .content()

    log.info("message: $message")
  }
}
