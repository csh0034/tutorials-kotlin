package com.ask.openfeignquerydsl.user.model

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.UuidGenerator

@Entity
@Table(name = "mt_user")
class User(

  @Id
  @UuidGenerator
  @Column(length = 50)
  var id: String? = null,

  @Embedded
  var name: Name,

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  var role: Role,

  @ColumnDefault("0")
  var count: Int = 0,
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

  override fun toString(): String {
    return "User(id=$id, name='$name', role=$role)"
  }
}

enum class Role {
  USER, ADMIN
}


@Embeddable
data class Name(
  @Column(nullable = false, length = 30)
  val name: String
)
