package com.ask.warmup.indicator

import org.slf4j.LoggerFactory
import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.system.measureTimeMillis

private val executor = Executors.newVirtualThreadPerTaskExecutor()

@Component
class WarmupHealthIndicator(
  private val onceWarmer: OnceWarmer,
) : HealthIndicator {
  override fun health(): Health {
    executor.submit { onceWarmer.run() }

    return if (onceWarmer.isDone()) {
      Health.up().build()
    } else {
      Health.down().build()
    }
  }
}

abstract class OnceWarmer {
  private val log = LoggerFactory.getLogger(javaClass)
  private val lock = ReentrantLock()

  @Volatile
  private var isDone = false

  fun run() {
    lock.withLock {
      if (!isDone) {
        log.info("{} started", javaClass.simpleName)
        val executionTime = measureTimeMillis { doRun() }
        log.info("{} completed, {}ms", javaClass.simpleName, executionTime)
        isDone = true
      }
    }
  }

  abstract fun doRun()

  fun isDone() = isDone
}

@Primary
@Component
class CompositeOnceWarmer(
  private val warmers: Collection<OnceWarmer>,
) : OnceWarmer() {
  override fun doRun() {
    executor.invokeAll(warmers.map { Callable { it.run() } })
  }
}

@Component
class Example1Warmer : OnceWarmer() {
  override fun doRun() {
    Thread.sleep(10_000)
  }
}

@Component
class Example2Warmer : OnceWarmer() {
  override fun doRun() {
    Thread.sleep(20_000)
  }
}
