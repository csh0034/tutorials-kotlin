package com.ask.springai

import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.ai.reader.tika.TikaDocumentReader
import org.springframework.core.io.ClassPathResource

class TikaDocumentReaderTest {
  private val log = LoggerFactory.getLogger(javaClass)
  private val resource = ClassPathResource("tax_with_markdown.docx")

  @Test
  fun read() {
    val tikaDocumentReader = TikaDocumentReader(resource)
    val documents = tikaDocumentReader.read()
    documents.forEach { log.info(it.text)}
  }
}
