package com.ask.springai.ui

import com.ask.springai.tool.DateTimeTool
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/tool")
class ToolController(
  private val chatClientBuilder: ChatClient.Builder,
  private val dateTimeTool: DateTimeTool,
) {
  @GetMapping
  fun tool(prompt: String): String? {
    val chatClient = chatClientBuilder.build()

    return chatClient.prompt()
      .advisors(SimpleLoggerAdvisor())
      .user(prompt)
      .tools(dateTimeTool)
      .toolContext(mapOf("id" to "spring-ai"))
      .call()
      .content()
  }
}
