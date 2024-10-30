package com.ask.jpa.account

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.Transient
import org.springframework.data.domain.Persistable
import java.util.UUID

@Entity
@Table(name = "mt_account")
class Account private constructor(
  @Id
  @Column(length = 50)
  private var id: AccountId,
) : Persistable<AccountId> {
  @Transient
  var new: Boolean = false

  override fun getId() = id

  override fun isNew() = new

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as Account

    return id == other.id
  }

  override fun hashCode(): Int {
    return id.hashCode()
  }

  companion object {
    fun create() = Account(AccountId.uuid()).apply { new = true }
  }
}

@JvmInline
value class AccountId(val value: String) {
  init {
    require(value.startsWith(ID_PREFIX)) { "must start with $ID_PREFIX, value: $value" }
  }

  companion object {
    const val ID_PREFIX = "a-"

    fun uuid() = AccountId(ID_PREFIX + UUID.randomUUID().toString())
  }
}
