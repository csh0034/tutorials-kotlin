package com.ask.core.client

import com.ask.core.config.QueryMap
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.service.annotation.DeleteExchange
import org.springframework.web.service.annotation.GetExchange
import org.springframework.web.service.annotation.PostExchange
import java.awt.print.Book

interface GoogleClient {
  @GetExchange("/")
  fun index(): String

  @GetExchange("/books/{id}")
  fun getBook(@PathVariable id: Long): Book

  @PostExchange("/books")
  fun saveBook(@RequestBody book: Book): Book

  @DeleteExchange("/books/{id}")
  fun deleteBook(@PathVariable id: Long): ResponseEntity<Unit>

  @GetExchange("/test")
  fun queryObject(@QueryMap dto: TestDto): List<Book>
}

data class TestDto(val id: Long, val name: String)
