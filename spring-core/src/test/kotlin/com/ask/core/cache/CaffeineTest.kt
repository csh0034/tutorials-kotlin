package com.ask.core.cache

import com.github.benmanes.caffeine.cache.Caffeine
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import java.util.SortedSet
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

private const val RANK = "rank"

class CaffeineTest : FunSpec({
  context("동작 검증") {
    lateinit var cache: LocalSortedSetCache<User>

    beforeTest {
      cache = LocalSortedSetCache()
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

    val cache = LocalSortedSetCache<String>()
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

data class TypedTuple<T : Comparable<T>>(
  val value: T,
  val score: Int,
) : Comparable<TypedTuple<T>> {
  override fun compareTo(other: TypedTuple<T>) = compareValuesBy(this, other, { it.score }, { it.value })
}

data class User(
  val id: Int,
  val name: String,
) : Comparable<User> {
  override fun compareTo(other: User) = compareValuesBy(this, other) { it.id }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as User

    return id == other.id
  }

  override fun hashCode(): Int {
    return id
  }
}

class LocalSortedSetCache<V : Comparable<V>> {
  private val cache = Caffeine.newBuilder()
    .maximumSize(1000)
    .build<String, SortedSet<TypedTuple<V>>> { sortedSetOf() }

  fun add(key: String, value: V, score: Int) {
    val set = cache.get(key)
    synchronized(set) {
      set.removeIf { it.value == value }
      set.add(TypedTuple(value, score))
    }
  }

  fun remove(key: String, value: V) {
    val set = cache.get(key)
    synchronized(set) {
      set.removeIf { it.value == value }
    }
  }

  fun top(key: String, n: Int): List<V> {
    val set = cache.get(key)
    synchronized(set) {
      return set.take(n).map { it.value }
    }
  }

  fun topWithScore(key: String, n: Int): List<TypedTuple<V>> {
    val set = cache.get(key)
    synchronized(set) {
      return set.take(n)
    }
  }

  fun topReverse(key: String, n: Int): List<V> {
    val set = cache.get(key)
    synchronized(set) {
      return set.toList().takeLast(n).reversed().map { it.value }
    }
  }

  fun rangeByScore(key: String, min: Int, max: Int): List<V> {
    val set = cache.get(key)
    synchronized(set) {
      return set.filter { it.score in min..max }.map { it.value }
    }
  }

  fun findByScore(key: String, score: Int): List<V> {
    val set = cache.get(key)
    synchronized(set) {
      return set.filter { it.score == score }.map { it.value }
    }
  }

  fun increaseScore(key: String, value: V, delta: Int) {
    val set = cache.get(key)
    synchronized(set) {
      val existing = set.find { it.value == value }
      if (existing != null) {
        set.remove(existing)
        set.add(TypedTuple(value, existing.score + delta))
      }
    }
  }
}
