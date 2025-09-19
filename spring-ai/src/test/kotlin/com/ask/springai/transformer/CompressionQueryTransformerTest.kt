package com.ask.springai.transformer

import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor
import org.springframework.ai.chat.messages.AssistantMessage
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.ai.rag.Query
import org.springframework.ai.rag.preretrieval.query.transformation.CompressionQueryTransformer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class CompressionQueryTransformerTest {
  private val log = LoggerFactory.getLogger(javaClass)

  @Autowired
  lateinit var chatClientBuilder: ChatClient.Builder

  @Test
  fun test() {
    val query = Query.builder()
      .text("미국은?")
      .history(
        UserMessage("한국의 수도는 어디입니까?"),
        AssistantMessage("서울 입니다.")
      )
      .build()

    val queryTransformer = CompressionQueryTransformer.builder()
      .chatClientBuilder(chatClientBuilder.defaultAdvisors(SimpleLoggerAdvisor()))
      .build()

    val transformedQuery = queryTransformer.transform(query)

    log.info(transformedQuery.toString())
  }
}
