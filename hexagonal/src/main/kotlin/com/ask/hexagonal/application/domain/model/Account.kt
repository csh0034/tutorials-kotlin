package com.ask.hexagonal.application.domain.model

class Account private constructor(
  val id: AccountId? = null,
  val name: Name,
) {
  companion object {
    fun withId(id: AccountId, name: Name) = Account(id, name)
    fun withoutId(name: Name) = Account(null, name)
  }
}

@JvmInline
value class AccountId(val value: String) {
  init {
    require(value.startsWith(ID_PREFIX)) { "accountId must start with a-, value: $value" }
  }

  companion object {
    const val ID_PREFIX = "a-"
  }
}

@JvmInline
value class Name(val value: String) {
  init {
    require(value.length < 10) { "name must be less than 10, value: $value" }
  }
}
