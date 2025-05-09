package com.ask.core.cache

import com.github.benmanes.caffeine.cache.Caffeine
import java.util.SortedSet

class CaffeineRankingCache<V : Comparable<V>> : RankingCache<V> {
  private val cache = Caffeine.newBuilder()
//    .expireAfterWrite(10, TimeUnit.MINUTES)
    .maximumSize(1000)
    .build<String, SortedSet<TypedTuple<V>>> { sortedSetOf() }

  override fun add(key: String, value: V, score: Int) {
    val set = cache.get(key)
    synchronized(set) {
      set.removeIf { it.value == value }
      set.add(TypedTuple(value, score))
    }
  }

  override fun remove(key: String, value: V) {
    val set = cache.get(key)
    synchronized(set) {
      set.removeIf { it.value == value }
    }
  }

  override fun top(key: String, n: Int): List<V> {
    val set = cache.get(key)
    synchronized(set) {
      return set.take(n).map { it.value }
    }
  }

  override fun topWithScore(key: String, n: Int): List<TypedTuple<V>> {
    val set = cache.get(key)
    synchronized(set) {
      return set.take(n)
    }
  }

  override fun topReverse(key: String, n: Int): List<V> {
    val set = cache.get(key)
    synchronized(set) {
      return set.toList().takeLast(n).reversed().map { it.value }
    }
  }

  override fun increaseScore(key: String, value: V, delta: Int) {
    val set = cache.get(key)
    synchronized(set) {
      val existing = set.find { it.value == value }
      if (existing != null) {
        set.remove(existing)
        set.add(TypedTuple(value, existing.score + delta))
      }
    }
  }

  override fun rangeByScore(key: String, min: Int, max: Int): List<V> {
    val set = cache.get(key)
    synchronized(set) {
      return set.filter { it.score in min..max }.map { it.value }
    }
  }

  override fun findByScore(key: String, score: Int): List<V> {
    val set = cache.get(key)
    synchronized(set) {
      return set.filter { it.score == score }.map { it.value }
    }
  }
}
