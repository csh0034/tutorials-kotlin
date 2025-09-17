package com.ask.springai.ui

import org.springframework.ai.reader.tika.TikaDocumentReader
import org.springframework.ai.transformer.splitter.TokenTextSplitter
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/etl")
class EtlController(
  private val vectorStore: VectorStore,
) {
  @PostMapping
  fun etl(@RequestParam file: MultipartFile, @RequestParam source: String) {
    val resource = file.resource
    val documents = TikaDocumentReader(resource).read()
    val splitter = TokenTextSplitter()

    documents.forEach { doc -> doc.metadata["source"] = source }
    val splitDocument = splitter.split(documents)

    vectorStore.add(splitDocument)
  }

  @DeleteMapping
  fun deleteEtl(@RequestParam key: String, @RequestParam value: String) {
    vectorStore.delete("$key == '$value'")
  }
}
