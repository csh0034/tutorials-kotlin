package com.ask.springai.transformer

import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor
import org.springframework.ai.rag.Query
import org.springframework.ai.rag.preretrieval.query.expansion.MultiQueryExpander
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class MultiQueryExpanderTest {
  private val log = LoggerFactory.getLogger(javaClass)

  @Autowired
  lateinit var chatClientBuilder: ChatClient.Builder

  @Test
  fun test() {
    val queryExpander = MultiQueryExpander.builder()
      .chatClientBuilder(chatClientBuilder.defaultAdvisors(SimpleLoggerAdvisor()))
      .numberOfQueries(3)
      .includeOriginal(false)
      .build()

    val query = Query.builder()
      .text("연봉 5000 만원인 직장인 종합소득세는?")
      .build()

    val queries = queryExpander.expand(query)

    queries.forEach {
      log.info("{}", it)
    }
  }
}
