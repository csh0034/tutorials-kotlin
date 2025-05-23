package com.ask.coroutine

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.concurrent.Executors
import java.util.concurrent.Future
import kotlin.system.measureTimeMillis

class VirtualThreadTest {
  @Test
  fun runVT() {
    val time = measureTimeMillis {
      val results = awaitAll {
        async { Thread.sleep(1000) }
        async { Thread.sleep(1000) }
        async { Thread.sleep(1000) }
      }

      assertThat(results.map { it.isSuccess }).hasSize(3)
    }

    println("Elapsed time: ${time}ms")
    assertThat(time).isLessThan(1500)
  }
}

private val virtualExecutor = Executors.newVirtualThreadPerTaskExecutor()

fun <T> awaitAll(block: AsyncCollector<T>.() -> Unit): List<Result<T>> {
  val collector = AsyncCollector<T>()
  collector.block()
  return collector.futures.map { runCatching { it.get() } }
}

class AsyncCollector<T> {
  val futures = mutableListOf<Future<T>>()

  fun async(action: () -> T) {
    futures += virtualExecutor.submit(action)
  }
}
