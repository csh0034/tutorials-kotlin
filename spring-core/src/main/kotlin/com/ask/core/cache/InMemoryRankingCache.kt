package com.ask.core.cache

import java.util.SortedSet

class InMemoryRankingCache<V : Comparable<V>> : RankingCache<V> {
  private val cache = mutableMapOf<String, SortedSet<TypedTuple<V>>>()

  override fun add(key: String, value: V, score: Int) {
    val set = cache.getOrPut(key) { sortedSetOf() }
    synchronized(set) {
      set.removeIf { it.value == value }
      set.add(TypedTuple(value, score))
    }
  }

  override fun remove(key: String, value: V) {
    val set = cache[key]
    set?.let {
      synchronized(it) {
        it.removeIf { it.value == value }
      }
    }
  }

  override fun top(key: String, n: Int): List<V> {
    val set = cache[key]
    set?.let {
      synchronized(it) {
        return it.take(n).map { it.value }
      }
    }
    return emptyList()
  }

  override fun topWithScore(key: String, n: Int): List<TypedTuple<V>> {
    val set = cache[key]
    set?.let {
      synchronized(it) {
        return it.take(n)
      }
    }
    return emptyList()
  }

  override fun topReverse(key: String, n: Int): List<V> {
    val set = cache[key]
    set?.let {
      synchronized(it) {
        return set.toList().takeLast(n).reversed().map { it.value }
      }
    }
    return emptyList()
  }

  override fun increaseScore(key: String, value: V, delta: Int) {
    val set = cache[key]
    set?.let {
      synchronized(it) {
        val existing = it.find { it.value == value }
        if (existing != null) {
          it.remove(existing)
          it.add(TypedTuple(value, existing.score + delta))
        }
      }
    }
  }

  override fun rangeByScore(key: String, min: Int, max: Int): List<V> {
    val set = cache[key]
    set?.let {
      synchronized(it) {
        return it.filter { it.score in min..max }.map { it.value }
      }
    }
    return emptyList()
  }

  override fun findByScore(key: String, score: Int): List<V> {
    val set = cache[key]
    set?.let {
      synchronized(it) {
        return it.filter { it.score == score }.map { it.value }
      }
    }
    return emptyList()
  }
}
