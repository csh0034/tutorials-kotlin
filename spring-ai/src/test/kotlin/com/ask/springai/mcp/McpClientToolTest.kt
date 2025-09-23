package com.ask.springai.mcp

import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class McpClientToolTest {
  private val log = LoggerFactory.getLogger(javaClass)

  @Autowired
  lateinit var chatClientBuilder: ChatClient.Builder

  @Test
  fun `mcp tool`() {
    val chatClient = chatClientBuilder.build()
    val userInput = "10분 후에 알람을 설정해 줄 수 있어?"

    val message = chatClient.prompt()
      .advisors(SimpleLoggerAdvisor())
      .user(userInput)
      .toolNames("spring_ai_mcp_client_date_mcp_current_date", "spring_ai_mcp_client_date_mcp_set_alarm")
      .call()
      .content()

    log.info("message: $message")
  }
}
