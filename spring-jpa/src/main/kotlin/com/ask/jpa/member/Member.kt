package com.ask.jpa.member

import com.ask.jpa.config.SECRET_KEY_PLACEHOLDER
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.ColumnTransformer
import org.hibernate.annotations.UuidGenerator

@Entity
@Table(name = "mt_member")
class Member(
  @Id
  @UuidGenerator
  @Column(length = 50)
  var id: String? = null,

  @Column(nullable = false)
  @ColumnTransformer(
    read = "cast(aes_decrypt(unhex(email), secret()) as char)",
    write = "hex(aes_encrypt(?, secret()))"
  )
  val email: String,

  @Column(nullable = false)
  @ColumnTransformer(
    read = "cast(aes_decrypt(unhex(phone), $SECRET_KEY_PLACEHOLDER) as char)",
    write = "hex(aes_encrypt(?, $SECRET_KEY_PLACEHOLDER))"
  )
  val phone: String,

  val name: String,
) {
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as Member

    return id == other.id
  }

  override fun hashCode(): Int {
    return id?.hashCode() ?: 0
  }
}
