package com.ask.resilience4j

import io.github.resilience4j.ratelimiter.RateLimiterRegistry
import io.github.resilience4j.ratelimiter.RequestNotPermitted
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class RateLimiterController(
  private val rateLimiterRegistry: RateLimiterRegistry,
) {
  @GetMapping("/rate-limiter/{id}")
  fun rateLimiter(@PathVariable("id") id: String): String {
    val rateLimiter = rateLimiterRegistry.rateLimiter(id)

    return try {
      rateLimiter.executeRunnable { /* normal */ }
      "success, availablePermissions: ${rateLimiter.metrics.availablePermissions}"
    } catch (e: RequestNotPermitted) {
      "failure"
    }
  }
}
