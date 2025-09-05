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
  fun delete() {
    vectorStore.delete("meta in ['meta1', 'meta2', 'meta3']")
  }

  @Test
  fun add() {
    val documents = listOf(
      Document(
        "Spring AI rocks!! Spring AI rocks!! Spring AI rocks!! Spring AI rocks!! Spring AI rocks!!",
        mapOf("meta" to "meta1"),
      ),
      Document(
        "The World is Big and Salvation Lurks Around the Corner",
        mapOf("meta" to "meta2"),
      ),
      Document(
        "You walk forward facing the past and you turn back toward the future.",
        mapOf("meta" to "meta3"),
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
