package com.ask.resilience4j

import io.github.resilience4j.ratelimiter.RateLimiter
import io.github.resilience4j.ratelimiter.RateLimiterConfig
import io.github.resilience4j.ratelimiter.RequestNotPermitted
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Duration
import java.util.concurrent.atomic.AtomicInteger

class RateLimiterTest {
  @Test
  fun rateLimiter() {
    val config = RateLimiterConfig.custom()
      .limitForPeriod(2)
      .limitRefreshPeriod(Duration.ofSeconds(1))
      .timeoutDuration(Duration.ofMillis(100))
      .build()

    val rateLimiter = RateLimiter.of("rateLimiter", config)
    val allowed = AtomicInteger()
    val blocked = AtomicInteger()

    repeat(3) {
      try {
        rateLimiter.executeRunnable { /* normal */ }
        allowed.incrementAndGet()
      } catch (e: RequestNotPermitted) {
        blocked.incrementAndGet()
      }
    }

    assertThat(allowed).hasValue(2)
    assertThat(blocked).hasValue(1)
  }
}
