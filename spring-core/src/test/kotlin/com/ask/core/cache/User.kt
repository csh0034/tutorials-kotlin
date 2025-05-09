package com.ask.core.cache

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

