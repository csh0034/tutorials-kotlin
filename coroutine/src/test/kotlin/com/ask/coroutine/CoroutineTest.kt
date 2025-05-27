package com.ask.coroutine

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import kotlin.system.measureTimeMillis

class CoroutineTest {
  @Test
  fun `delay Dispatchers IO`() = runBlocking {
    val time = measureTimeMillis {
      val deferred1 = async(Dispatchers.IO) { delayData() }
      val deferred2 = async(Dispatchers.IO) { delayData() }
      val deferred3 = async(Dispatchers.IO) { delayData() }

      // 결과 수집
      val results = awaitAll(deferred1, deferred2, deferred3)

      println("Results: $results")
    }

    println("Elapsed time: ${time}ms")
  }

  @Test
  fun `delay EmptyCoroutineContext`() {
    val test = runBlocking {
      val deferred1 = async { delayData() }
      val deferred2 = async { delayData() }
      val deferred3 = async { delayData() }

      // 결과 수집
      val results = awaitAll(deferred1, deferred2, deferred3)

      "Results: $results"
    }

    println(test)
  }

  @Test
  fun `sleep Dispatchers IO`() = runBlocking {
    val time = measureTimeMillis {
      val deferred1 = async(Dispatchers.IO) { sleepData() }
      val deferred2 = async(Dispatchers.IO) { sleepData() }
      val deferred3 = async(Dispatchers.IO) { sleepData() }

      // 결과 수집
      val results = awaitAll(deferred1, deferred2, deferred3)

      println("Results: $results")
    }

    println("Elapsed time: ${time}ms")
  }

  @Test
  fun `sleep EmptyCoroutineContext`() = runBlocking {
    val time = measureTimeMillis {
      val deferred1 = async { sleepData() }
      val deferred2 = async { sleepData() }
      val deferred3 = async { sleepData() }

      // 결과 수집
      val results = awaitAll(deferred1, deferred2, deferred3)

      println("Results: $results")
    }

    println("Elapsed time: ${time}ms")
  }

  @Test
  fun sleepWithCoroutineScope() {
    // 기다리지 않고 바로 종료됨
    CoroutineScope(Dispatchers.IO).launch { sleepDataWithCoroutineScope() }
  }

  suspend fun delayData(): String {
    delay(1000)
    println(Thread.currentThread().name)
    return "Hello, Coroutine!"
  }

  suspend fun sleepDataWithCoroutineScope() = coroutineScope {
    val deferred1 = async { sleepData() }
    val deferred2 = async { sleepData() }
    val deferred3 = async { sleepData() }

    // 결과 수집
    val result1 = deferred1.await()
    val result2 = deferred2.await()
    val result3 = deferred3.await()

    "Results: $result1, $result2, $result3"
  }

  fun sleepData(): String {
    Thread.sleep(1000)
    println(Thread.currentThread().name)
    return "Hello, Coroutine!"
  }
}
