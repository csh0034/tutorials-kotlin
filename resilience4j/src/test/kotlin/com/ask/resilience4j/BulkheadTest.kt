package com.ask.resilience4j

import io.github.resilience4j.bulkhead.Bulkhead
import io.github.resilience4j.bulkhead.BulkheadConfig
import io.github.resilience4j.bulkhead.BulkheadFullException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Duration
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

class BulkheadTest {
  @Test
  fun bulkhead() {
    val config = BulkheadConfig.custom()
      .maxConcurrentCalls(2)
      .maxWaitDuration(Duration.ZERO)
      .build()

    val bulkhead = Bulkhead.of("bulk", config)

    val service = Executors.newFixedThreadPool(3)
    val latch = CountDownLatch(3)
    val fail = AtomicInteger()

    repeat(3) {
      service.execute {
        try {
          bulkhead.executeRunnable {
            Thread.sleep(1000)
          }
        } catch (e: BulkheadFullException) {
          fail.incrementAndGet()
        } finally {
          latch.countDown()
        }
      }
    }

    latch.await()

    assertThat(fail.get()).isOne()
  }
}
