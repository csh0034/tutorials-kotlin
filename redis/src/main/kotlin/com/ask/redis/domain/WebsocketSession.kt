package com.ask.redis.domain

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.domain.Persistable
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.index.Indexed

@RedisHash
class WebsocketSession private constructor(
  @Id
  private val sessionId: String,

  @Indexed
  val userId: String,

  @Indexed
  var server: String,
) : Persistable<String> {
  @Transient
  var new: Boolean = false

  override fun getId() = sessionId

  override fun isNew() = new

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as WebsocketSession

    return sessionId == other.sessionId
  }

  override fun hashCode(): Int {
    return sessionId.hashCode()
  }

  override fun toString(): String {
    return "WebsocketSession(sessionId='$sessionId', userId='$userId', server='$server')"
  }

  companion object {
    fun of(sessionId: String, userId: String, server: String) = WebsocketSession(sessionId, userId, server).apply { new = true }
  }
}
