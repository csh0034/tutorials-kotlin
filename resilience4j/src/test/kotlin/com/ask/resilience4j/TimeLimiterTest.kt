package com.ask.resilience4j

import io.github.resilience4j.timelimiter.TimeLimiter
import io.github.resilience4j.timelimiter.TimeLimiterConfig
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.Test
import java.time.Duration
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeoutException

class TimeLimiterTest {
  @Test
  fun timelimiter() {
    val config = TimeLimiterConfig.custom()
      .timeoutDuration(Duration.ofMillis(1000))
      .cancelRunningFuture(true) // 제한 시간 초과 시 실행 중인 작업 취소
      .build()

    val timeLimiter = TimeLimiter.of("timeLimiter", config)

    assertThatExceptionOfType(TimeoutException::class.java).isThrownBy {
      timeLimiter.executeFutureSupplier {
        CompletableFuture.supplyAsync {
          Thread.sleep(2000)
        }
      }
    }
  }
}
