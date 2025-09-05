package com.ask.springai

import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.ai.embedding.EmbeddingModel
import org.springframework.ai.vectorstore.SimpleVectorStore
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class CosineSimilarityTest {
  private val log = LoggerFactory.getLogger(javaClass)

  @Autowired
  lateinit var embeddingModel: EmbeddingModel

  @Test
  fun similaritySearch() {
    val kingVector = embeddingModel.embed("king")
    val queenVector = embeddingModel.embed("queen")

    val cosineSimilarity = SimpleVectorStore.EmbeddingMath.cosineSimilarity(kingVector, queenVector)
    log.info("cosineSimilarity: {}", cosineSimilarity)
  }
}
