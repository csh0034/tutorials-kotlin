package com.ask.coroutine

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.Future
import kotlin.system.measureTimeMillis

class VirtualThreadTest {
  @Test
  fun awaitAll() {
    val time = measureTimeMillis {
      val results = awaitAll<Int> {
        async { Thread.sleep(1000); 1 }
        async { Thread.sleep(1000); 2 }
        async { Thread.sleep(1000); 3 }
      }

      results.map { it.getOrNull() }.forEach { println(it) }

      assertThat(results.map { it.isSuccess }).hasSize(3)
    }

    println("Elapsed time: ${time}ms")
    assertThat(time).isLessThan(1500)
  }

  @Test
  fun awaitAllUnit() {
    val time = measureTimeMillis {
      val results = awaitAllUnit {
        async { Thread.sleep(1000) }
        async { Thread.sleep(1000) }
        async { Thread.sleep(1000) }
      }

      results.map { it.getOrNull() }.forEach { println(it) }

      assertThat(results.map { it.isSuccess }).hasSize(3)
    }

    println("Elapsed time: ${time}ms")
    assertThat(time).isLessThan(1500)
  }

  @Test
  fun asyncScope() {
    val time = measureTimeMillis {
      val countDownLatch = CountDownLatch(2)

      AsyncScope().async { Thread.sleep(1000); countDownLatch.countDown() }
      AsyncScope().async { Thread.sleep(1000); countDownLatch.countDown() }

      countDownLatch.await()
    }

    assertThat(time).isLessThan(1500)
  }
}

private val virtualExecutor = Executors.newVirtualThreadPerTaskExecutor()

fun <T> Future<T>.await(): T = this.get()

@Suppress("UNCHECKED_CAST")
fun <T> awaitAll(block: AsyncScope.() -> Unit): List<Result<T>> {
  val scope = AsyncScope()
  scope.block()
  return scope.futures.map { runCatching { it.await() as T } }
}

fun awaitAllUnit(block: AsyncScope.() -> Unit) = awaitAll<Unit>(block)

class AsyncScope {
  val futures = mutableListOf<Future<*>>()

  fun <T> async(action: () -> T): Future<T> {
    val future = virtualExecutor.submit(action)
    futures += future
    return future
  }
}
