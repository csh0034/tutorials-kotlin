package com.ask.springai

import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor
import org.springframework.ai.vectorstore.SearchRequest
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class QuestionAnswerAdvisorTest {
  private val log = LoggerFactory.getLogger(javaClass)

  @Autowired
  lateinit var chatClientBuilder: ChatClient.Builder

  @Autowired
  lateinit var vectorStore: VectorStore

  @Test
  fun rag() {
    val advisor = QuestionAnswerAdvisor.builder(vectorStore)
      .searchRequest(SearchRequest.builder().similarityThreshold(0.5).topK(3).build())
      .build()

    val chatClient = chatClientBuilder
      .defaultAdvisors(advisor)
      .build()

    val userInput = "연봉 5,000 만원인 거주자 종합소득세는?"

    val message = chatClient.prompt()
      .user(userInput)
      .call()
      .content()

    log.info("message: $message")
  }
}
