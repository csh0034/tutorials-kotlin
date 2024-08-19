package com.ask.jpa.vote

import com.ask.jpa.user.User
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.UuidGenerator
import java.time.Instant

@Entity
@Table(name = "mt_vote_item")
class VoteItem(
  @Id
  @UuidGenerator
  @Column(length = 50)
  var id: String? = null,

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  val vote: Vote,

  @Column(updatable = false)
  var createdAt: Instant = Instant.now()
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
