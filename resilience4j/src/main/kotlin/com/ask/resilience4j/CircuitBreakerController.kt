package com.ask.resilience4j

import com.ask.resilience4j.client.BookClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class CircuitBreakerController(
  private val bookClient: BookClient,
) {
  @GetMapping("/circuit-breaker")
  fun circuitBreaker(): String {
    return bookClient.index()
  }
}
