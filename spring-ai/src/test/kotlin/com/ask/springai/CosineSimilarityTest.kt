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

  /**
   * openai, text-embedding-3-large
   * 1. king, 왕 0.547488527592143
   * 2. king, queen 0.5524403169396224
   * 3. king, emperor 0.4733115273460669
   *
   * ollama, mxbai-embed-large
   * 1. king, 왕 0.6004118918145036
   * 2. king, queen 0.6937793761351896
   * 3. king, emperor 0.6646399499797939
   */
  @Test
  fun similaritySearch() {
    val kingVector = embeddingModel.embed("king")
    val queenVector = embeddingModel.embed("emperor")

    val cosineSimilarity = SimpleVectorStore.EmbeddingMath.cosineSimilarity(kingVector, queenVector)
    log.info("cosineSimilarity: {}", cosineSimilarity)
  }
}
