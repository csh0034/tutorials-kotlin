package com.ask.springai

import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.ai.document.Document
import org.springframework.ai.reader.tika.TikaDocumentReader
import org.springframework.ai.transformer.splitter.TokenTextSplitter
import org.springframework.core.io.ClassPathResource

/**
 * @see <a href="https://github.com/spring-projects/spring-ai/issues/2123">overlap 기능추가에 대한 spring ai issue<a>
 */
class TokenTextSplitterTest {
  private val log = LoggerFactory.getLogger(javaClass)

  @Test
  fun splitter() {
    val doc1 = Document(
      "This is a long piece of text that needs to be split into smaller chunks for processing.",
      mapOf("source" to "example.txt")
    )

    val doc2 = Document(
      "Another document with content that will be split based on token count.",
      mapOf("source" to "example2.txt")
    )

    val splitter = TokenTextSplitter()
    val splitDocuments = splitter.apply(listOf(doc1, doc2))

    for (doc in splitDocuments) {
      log.info("Chunk: {}", doc.text)
      log.info("Metadata: {}", doc.metadata)
    }
  }

  @Test
  fun splitter2() {
    val splitter = TokenTextSplitter.builder()
      .withChunkSize(1_500)
      .withMinChunkSizeChars(350)
      .withMinChunkLengthToEmbed(5)
      .withMaxNumChunks(10_000)
      .withKeepSeparator(true)
      .build()

    val documents = TikaDocumentReader(ClassPathResource("tax_with_markdown.docx")).read()
    val splitDocuments = splitter.apply(documents)

    splitDocuments.forEach { log.info(it.text) }
  }
}
