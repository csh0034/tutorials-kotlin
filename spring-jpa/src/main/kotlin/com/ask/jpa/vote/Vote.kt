package com.ask.jpa.vote

import com.ask.jpa.user.User
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.OrderBy
import jakarta.persistence.Table
import org.hibernate.annotations.UuidGenerator

@Entity
@Table(name = "mt_vote")
class Vote(
  @Id
  @UuidGenerator
  @Column(length = 50)
  var id: String? = null,

  @Column(length = 30, nullable = false)
  var title: String,

  @OrderBy("createdAt")
  @OneToMany(mappedBy = "vote", cascade = [CascadeType.ALL], orphanRemoval = true)
  val voteItems: MutableList<VoteItem> = mutableListOf(),
) {
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as User

    return id == other.id
  }

  override fun hashCode(): Int {
    return id?.hashCode() ?: 0
  }
}
