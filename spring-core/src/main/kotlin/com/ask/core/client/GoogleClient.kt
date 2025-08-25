package com.ask.core.client

import com.ask.core.config.QueryMap
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.service.annotation.DeleteExchange
import org.springframework.web.service.annotation.GetExchange
import org.springframework.web.service.annotation.PostExchange
import java.awt.print.Book

interface GoogleClient {
  @GetExchange("/")
  fun index(): String

  @GetExchange("/books/{id}")
  fun getBook(@PathVariable id: Long): String

  @PostExchange("/books")
  fun saveBook(@RequestBody book: Book): String

  @DeleteExchange("/books/{id}")
  fun deleteBook(@PathVariable id: Long): ResponseEntity<Unit>

  @GetExchange("/test")
  fun queryMap(@QueryMap dto: TestDto): List<String>

  @GetExchange("/test")
  fun queryMap(@RequestParam map: Map<String, Any>): List<String>
}

data class TestDto(
  val id: Long,
  val names: List<String>,
)
