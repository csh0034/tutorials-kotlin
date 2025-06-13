package com.ask.virtualthread

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.concurrent.Executors
import java.util.concurrent.Semaphore
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class ConcurrencyTest {

  @Test
  fun reentrantLock() {
    val lock = ReentrantLock()
    var count = 0

    Executors.newVirtualThreadPerTaskExecutor().use {
      (1..100).forEach { _ ->
        it.execute {
          lock.lock()
          try {
            val tmp = count
            Thread.sleep(1)
            count = tmp + 1
          } finally {
            lock.unlock()
          }
        }
      }
    }

    assertThat(count).isEqualTo(100)
  }

  @Test
  fun `reentrantLock with kotlin function`() {
    val lock = ReentrantLock()
    var count = 0

    Executors.newVirtualThreadPerTaskExecutor().use {
      (1..100).forEach { _ ->
        it.execute {
          lock.withLock {
            val tmp = count
            Thread.sleep(1)
            count = tmp + 1
          }
        }
      }
    }

    assertThat(count).isEqualTo(100)
  }

  @Test
  fun semaphore() {
    val semaphore = Semaphore(1)
    var count = 0

    Executors.newVirtualThreadPerTaskExecutor().use {
      (1..100).forEach { _ ->
        it.execute {
          semaphore.acquire()
          try {
            val tmp = count
            Thread.sleep(1)
            count = tmp + 1
          } finally {
            semaphore.release()
          }
        }
      }
    }

    assertThat(count).isEqualTo(100)
  }
}
