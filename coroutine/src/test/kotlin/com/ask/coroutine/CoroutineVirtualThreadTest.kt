package com.ask.coroutine

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.util.concurrent.Executors
import kotlin.system.measureTimeMillis

private val dispatcher =run {
  val factory = Thread.ofVirtual().name("VT").factory()
  Executors.newThreadPerTaskExecutor(factory).asCoroutineDispatcher() + CoroutineName("io")
}

/**
 * VT + Coroutine 사용시 sleep 을 해도 문제 없음
 */
class CoroutineVirtualThreadTest {
  @Test
  fun runCoroutine() = runBlocking {
    val time = measureTimeMillis {
      val job1 = launch(dispatcher) { sleepData() }
      val job2 = launch(dispatcher) { sleepData() }
      val job3 = launch(dispatcher) { sleepData() }

      joinAll(job1, job2, job3)
    }

    println("Elapsed time: ${time}ms")
  }

  private fun sleepData(): String {
    Thread.sleep(1000)
    println(Thread.currentThread().name)
    return "Hello, Coroutine!"
  }
}
