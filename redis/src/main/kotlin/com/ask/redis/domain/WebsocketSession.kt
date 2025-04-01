package com.ask.redis.domain

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.domain.Persistable
import org.springframework.data.redis.core.RedisHash

@RedisHash("websocket-session")
class WebsocketSession private constructor(
  @Id
  private val sessionId: String,

  val userId: String,
) : Persistable<String> {
  @Transient
  var new: Boolean = false

  override fun getId() = sessionId

  override fun isNew() = new

  companion object {
    fun of(sessionId: String, userId: String) = WebsocketSession(sessionId, userId).apply { new = true }
  }
}
