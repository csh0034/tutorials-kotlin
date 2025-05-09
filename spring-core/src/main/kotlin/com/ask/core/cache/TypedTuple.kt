package com.ask.core.cache

data class TypedTuple<T : Comparable<T>>(
  val value: T,
  val score: Int,
) : Comparable<TypedTuple<T>> {
  override fun compareTo(other: TypedTuple<T>) = compareValuesBy(this, other, { it.score }, { it.value })
}
