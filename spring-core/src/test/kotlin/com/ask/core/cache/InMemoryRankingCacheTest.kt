package com.ask.core.cache

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

private const val RANK = "rank"

class InMemoryRankingCacheTest : FunSpec({
  context("동작 검증") {
    lateinit var cache: RankingCache<User>

    beforeTest {
      cache = InMemoryRankingCache()
      cache.add(RANK, User(1, "userA"), 100)
      cache.add(RANK, User(2, "userB"), 200)
      cache.add(RANK, User(3, "userC"), 150)
      cache.add(RANK, User(4, "userD"), 180)
      cache.add(RANK, User(5, "userE"), 170)
      cache.add(RANK, User(6, "userF"), 90)
    }

    test("스코어가 낮은 순서로 3개 가져 오기") {
      val result = cache.top(RANK, 3)
      result.map { it.id } shouldContainExactly listOf(6, 1, 3)
    }

    test("스코어가 낮은 순서로 3개 가져 오기, 스코어 포함") {
      val result = cache.topWithScore(RANK, 3)
      result.map { it.value.id } shouldContainExactly listOf(6, 1, 3)
      result.map { it.score } shouldContainExactly listOf(90, 100, 150)
    }

    test("스코어가 높은 순서로 3개 가져 오기") {
      val result = cache.topReverse(RANK, 3)
      result.map { it.id } shouldContainExactly listOf(2, 4, 5)
    }

    test("특정 스코어 사이 대상 가져 오기") {
      val result = cache.rangeByScore(RANK, 120, 180)
      result.map { it.id } shouldContainExactly listOf(3, 5, 4)
    }

    test("값 갱신후 변경된 대상 가져 오기") {
      cache.add(RANK, User(3, "userC"), 80)
      val result = cache.top(RANK, 3)
      result.map { it.id } shouldContainExactly listOf(3, 6, 1)
    }

    test("특정 대상 삭제후 변경된 대상 가져 오기") {
      cache.remove(RANK, User(1, "userA"))
      val result = cache.top(RANK, 3)
      result.map { it.id } shouldContainExactly listOf(6, 3, 5)
    }

    test("스코어로 찾기") {
      val result = cache.findByScore(RANK, 90)
      result.map { it.id } shouldContainExactly listOf(6)
    }

    test("스코어 증가") {
      cache.increaseScore(RANK, User(1, "userA"), 300)
      val result = cache.findByScore(RANK, 400)
      result.map { it.id } shouldContainExactly listOf(1)
    }
  }

  context("동시성 테스트") {
    val value = "userA"

    val cache = InMemoryRankingCache<String>()
    cache.add(RANK, value, 0)

    test("스코어 증가") {
      val delta = 1
      val threadCount = 50
      val latch = CountDownLatch(threadCount)
      val executor = Executors.newFixedThreadPool(threadCount)

      repeat(threadCount) {
        executor.execute {
          cache.increaseScore(RANK, value, delta)
          latch.countDown()
        }
      }

      latch.await()
      executor.shutdown()

      cache.topWithScore(RANK, 1).first().score shouldBe delta * threadCount
    }
  }
})
