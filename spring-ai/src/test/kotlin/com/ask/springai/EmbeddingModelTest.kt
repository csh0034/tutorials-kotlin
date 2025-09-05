package com.ask.springai

import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.ai.embedding.EmbeddingModel
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class EmbeddingModelTest {
  private val log = LoggerFactory.getLogger(javaClass)

  @Autowired
  lateinit var embeddingModel: EmbeddingModel

  @Test
  fun dimensions() {
    val dimensions = embeddingModel.dimensions()
    log.info("dimensions: {}", dimensions)
  }

  @Test
  fun embed() {
    val embed = embeddingModel.embed("King")
    log.info("embed: {}", embed)
  }
}
