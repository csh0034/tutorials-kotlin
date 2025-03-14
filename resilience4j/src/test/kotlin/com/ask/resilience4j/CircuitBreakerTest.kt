package com.ask.resilience4j

import io.github.resilience4j.circuitbreaker.CallNotPermittedException
import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.circuitbreaker.CircuitBreaker.State
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.assertj.core.api.Assertions.assertThatIllegalStateException
import org.assertj.core.api.Assertions.assertThatNoException
import org.junit.jupiter.api.Test

/**
 * Count-based 인 경우 minimumNumberOfCalls 값은 Math.min(minimumNumberOfCalls, slidingWindowSize) 으로 계산됨
 */
class CircuitBreakerTest {
  @Test
  fun circuitBreaker() {
    val config = CircuitBreakerConfig.custom()
      .slidingWindowSize(4)
      .failureRateThreshold(50f)
      .build()

    val circuitBreaker = CircuitBreaker.of("circleBreaker", config)

    assertThat(circuitBreaker.state).isEqualTo(State.CLOSED)

    repeat(2) {
      assertThatIllegalStateException().isThrownBy {
        circuitBreaker.executeRunnable { throw IllegalStateException("exception occurred") }
      }
    }

    assertThat(circuitBreaker.state).isEqualTo(State.CLOSED)

    repeat(2) {
      assertThatNoException().isThrownBy {
        circuitBreaker.executeRunnable { /* normal */ }
      }
    }

    assertThat(circuitBreaker.state).isEqualTo(State.OPEN)

    assertThatExceptionOfType(CallNotPermittedException::class.java).isThrownBy {
      circuitBreaker.executeRunnable { /* normal */ }
    }
  }
}
