package com.ask.springaimcpserver.config

import com.ask.springaimcpserver.tool.DateTimeTool
import org.springframework.ai.tool.ToolCallbackProvider
import org.springframework.ai.tool.method.MethodToolCallbackProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ToolConfig(
  private val dateTimeTool: DateTimeTool,
) {
  @Bean
  fun toolCallbackProvider(): ToolCallbackProvider {
    return MethodToolCallbackProvider.builder()
      .toolObjects(dateTimeTool)
      .build()
  }
}
