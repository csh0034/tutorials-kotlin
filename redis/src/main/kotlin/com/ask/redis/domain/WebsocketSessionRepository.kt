package com.ask.redis.domain

import org.springframework.data.repository.CrudRepository

interface WebsocketSessionRepository : CrudRepository<WebsocketSession, String> {
  fun findByServer(server: String): List<WebsocketSession>
}
