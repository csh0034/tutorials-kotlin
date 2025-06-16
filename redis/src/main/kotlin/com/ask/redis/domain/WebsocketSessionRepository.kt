package com.ask.redis.domain

import org.springframework.data.redis.core.PartialUpdate
import org.springframework.data.redis.core.RedisKeyValueTemplate
import org.springframework.data.repository.CrudRepository

interface WebsocketSessionRepository : CrudRepository<WebsocketSession, String>, WebsocketSessionRepositoryCustom {
  fun findByServer(server: String): List<WebsocketSession>
}

interface WebsocketSessionRepositoryCustom {
  fun update(update: PartialUpdate<WebsocketSession>)
}

class WebsocketSessionRepositoryCustomImpl(
  private val redisKeyValueTemplate: RedisKeyValueTemplate,
) : WebsocketSessionRepositoryCustom {
  override fun update(partialUpdate: PartialUpdate<WebsocketSession>) {
    redisKeyValueTemplate.update(partialUpdate)
  }
}
