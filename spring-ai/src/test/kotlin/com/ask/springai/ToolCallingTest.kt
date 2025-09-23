package com.ask.springai

import com.ask.springai.tool.DateTimeTool
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class ToolCallingTest {
  private val log = LoggerFactory.getLogger(javaClass)

  @Autowired
  lateinit var chatClientBuilder: ChatClient.Builder

  @Autowired
  lateinit var dateTimeTool: DateTimeTool

  @Test
  fun tool() {
    val chatClient = chatClientBuilder.build()
    val userInput = "10분 후에 알람을 설정해 줄 수 있어?"

    val message = chatClient.prompt()
      .advisors(SimpleLoggerAdvisor())
      .user(userInput)
      .tools(dateTimeTool)
      .toolContext(mapOf("id" to "spring-ai"))
      .call()
      .content()

    log.info("message: $message")
  }
}
