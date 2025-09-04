package com.ask.springai

import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.ai.document.Document
import org.springframework.ai.vectorstore.SearchRequest
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class VectorStoreTest {
  private val log = LoggerFactory.getLogger(javaClass)

  @Autowired
  lateinit var vectorStore: VectorStore

  @Test
  fun add() {
    val documents = listOf(
      Document(
        "Spring AI rocks!! Spring AI rocks!! Spring AI rocks!! Spring AI rocks!! Spring AI rocks!!",
        mapOf("meta1" to "meta1")
      ),
      Document("The World is Big and Salvation Lurks Around the Corner"),
      Document(
        "You walk forward facing the past and you turn back toward the future.",
        mapOf("meta2" to "meta2")
      )
    )

    vectorStore.add(documents)
  }

  @Test
  fun similaritySearch() {
    val request = SearchRequest.builder()
      .query("Spring")
      .topK(5)
      .build()

    val result = vectorStore.similaritySearch(request)
    result.forEach { log.info(it.toString()) }
  }
}
