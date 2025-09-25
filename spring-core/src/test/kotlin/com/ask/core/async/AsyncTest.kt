package com.ask.core.async

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

@SpringBootTest
class AsyncTest {
  @Autowired
  lateinit var asyncService: AsyncService

  @Test
  fun test() {
    // CompletableFuture 병렬 실행
    val futures = (1..10).map { asyncService.async() }

    // n초 동안 대기하며 지날 경우 예외 무시
    futures.awaitAll(1000)

    // n초 안에 처리될 경우 응답 반환 아닌 경우 default 반환
    val results = futures.completedOrDefault("fail")

    // 1초 안에 처리된 경우에만 fail 이 아니게 응답
    results.forEach(::println)
  }
}

fun <T> List<CompletableFuture<T>>.awaitAll(millis: Long) {
  runCatching { CompletableFuture.allOf(*this.toTypedArray()).get(millis, TimeUnit.MILLISECONDS) }
}

fun <T> List<CompletableFuture<T>>.completedOrDefault(default: T): List<T> {
  return this.map { if (it.isDone) it.get() else default }
}

@Component
class AsyncService {
  @Async
  fun async(): CompletableFuture<String> {
    val sleepMillis = (900..1100).random().toLong()
    Thread.sleep(sleepMillis) // db 조회 등
    return CompletableFuture.completedFuture("hello!!, ${Thread.currentThread().name}, $sleepMillis ms")
  }
}
