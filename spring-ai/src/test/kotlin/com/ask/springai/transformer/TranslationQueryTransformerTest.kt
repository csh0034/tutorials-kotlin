package com.ask.springai.transformer

import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor
import org.springframework.ai.rag.Query
import org.springframework.ai.rag.preretrieval.query.transformation.TranslationQueryTransformer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TranslationQueryTransformerTest {
  private val log = LoggerFactory.getLogger(javaClass)

  @Autowired
  lateinit var chatClientBuilder: ChatClient.Builder

  @Test
  fun test() {
    val query = Query.builder()
      .text("대한민국의 수도는 어디인가요?")
      .build()

    val queryTransformer = TranslationQueryTransformer.builder()
      .chatClientBuilder(chatClientBuilder.defaultAdvisors(SimpleLoggerAdvisor()))
      .targetLanguage("english")
      .build()

    val transformedQuery = queryTransformer.transform(query)

    log.info(transformedQuery.toString())
  }
}
