package org.ask.apns

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.concurrent.CompletableFuture

class CompletableFutureTest {
  @Test
  fun get() {
    val completableFuture = CompletableFuture<String>()
    completableFuture.complete("test")
    val result = completableFuture.get()
    assertThat(result).isEqualTo("test")
  }

  @Test
  fun whenComplete() {
    CompletableFuture<String>()
      .completeAsync { Thread.sleep(1000); "test" }
      .whenComplete { response, _ -> println("${Thread.currentThread().name}, $response") }

    Thread.sleep(2000)
  }

  @Test
  fun supplyAsync() {
    for (i in 1..10) {
      CompletableFuture.supplyAsync { "hello" }
        .whenComplete { t, _ -> println("[${Thread.currentThread().name}] $t") }
    }
  }
}
