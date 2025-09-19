package com.ask.springai

import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor
import org.springframework.ai.chat.memory.ChatMemory
import org.springframework.ai.chat.messages.UserMessage
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
  fun `with PromptChatMemoryAdvisor`() {
    val advisor = PromptChatMemoryAdvisor.builder(chatMemory)
      .build()

    val chatClient = chatClientBuilder
      .defaultAdvisors(advisor, SimpleLoggerAdvisor())
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

  @Test
  fun `with MessageChatMemoryAdvisor`() {
    chatMemory.add("cid", UserMessage.builder().text("파인 튜닝에 대해 설명해줘").build())

    val advisor = MessageChatMemoryAdvisor.builder(chatMemory)
      .build()

    val chatClient = chatClientBuilder
      .defaultAdvisors(advisor, SimpleLoggerAdvisor())
      .build()

    val userInput = "llm rag 에 대해서 설명해줘"

    val message = chatClient.prompt()
      .user(userInput)
      .advisors { a -> a.param(ChatMemory.CONVERSATION_ID, "cid") }
      .call()
      .content()

    log.info("message: $message")
  }
}
