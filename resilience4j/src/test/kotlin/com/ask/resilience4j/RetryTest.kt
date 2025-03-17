package com.ask.resilience4j

import io.github.resilience4j.core.IntervalFunction
import io.github.resilience4j.retry.Retry
import io.github.resilience4j.retry.RetryConfig
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatIllegalStateException
import org.junit.jupiter.api.Test
import java.util.concurrent.atomic.AtomicInteger

class RetryTest {
  @Test
  fun retry() {
    val config = RetryConfig.custom<Any>()
      .maxAttempts(3) // 최초 요청도 횟수에 포함
      .intervalFunction(IntervalFunction.ofExponentialBackoff())
      .retryExceptions(IllegalStateException::class.java)
      .build()

    val retry = Retry.of("retry", config)
    val retryCount = AtomicInteger()

    assertThatIllegalStateException().isThrownBy {
      retry.executeRunnable {
        retryCount.incrementAndGet()
        throw IllegalStateException("exception occurred")
      }
    }

    assertThat(retryCount).hasValue(3)
  }
}
