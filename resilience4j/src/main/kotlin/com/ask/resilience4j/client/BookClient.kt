package com.ask.resilience4j.client

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.service.annotation.GetExchange

interface BookClient {
  @CircuitBreaker(name = "default", fallbackMethod = "fallbackIndex")
  @GetExchange("/")
  fun index(): String

  fun fallbackIndex(e: Throwable): String {
    return "failure"
  }

  @GetExchange("/books/{id}")
  fun getBook(@PathVariable id: Long): String
}
