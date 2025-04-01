package com.ask.redis.domain

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import org.redisson.spring.starter.RedissonAutoConfigurationV2
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest
import org.springframework.context.annotation.Import

@DataRedisTest
@Import(RedissonAutoConfigurationV2::class)
class WebsocketSessionRepositoryTest {
  @Autowired
  lateinit var websocketSessionRepository: WebsocketSessionRepository

  @Test
  fun `hash 저장`() {
    val session = WebsocketSession.of("session01", "user01")
    websocketSessionRepository.save(session)
  }

  companion object {
    @JvmStatic
    @AfterAll
    fun tearDown(@Autowired websocketSessionRepository: WebsocketSessionRepository) {
      websocketSessionRepository.deleteAll()
    }
  }
}
