package com.ask.core.cache

interface RankingCache<V : Comparable<V>> {
  fun add(key: String, value: V, score: Int)
  fun remove(key: String, value: V)
  fun top(key: String, n: Int): List<V>
  fun topWithScore(key: String, n: Int): List<TypedTuple<V>>
  fun topReverse(key: String, n: Int): List<V>
  fun increaseScore(key: String, value: V, delta: Int)
  fun rangeByScore(key: String, min: Int, max: Int): List<V>
  fun findByScore(key: String, score: Int): List<V>
}
