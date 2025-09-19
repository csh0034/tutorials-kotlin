package com.ask.springai.transformer

import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor
import org.springframework.ai.rag.Query
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class RewriteQueryTransformerTest {
  private val log = LoggerFactory.getLogger(javaClass)

  @Autowired
  lateinit var chatClientBuilder: ChatClient.Builder

  @Test
  fun test() {
    val query = Query.builder()
      .text("나는 지금 머신러닝 공부중인데 llm이 뭐야?")
      .build()

    val queryTransformer = RewriteQueryTransformer.builder()
      .chatClientBuilder(chatClientBuilder.defaultAdvisors(SimpleLoggerAdvisor()))
      .build()

    val transformedQuery = queryTransformer.transform(query)

    log.info(transformedQuery.toString())
  }
}
