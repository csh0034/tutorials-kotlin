package com.ask.redis.domain

import com.ask.redis.config.RedisConfig
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.redisson.spring.starter.RedissonAutoConfigurationV2
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest
import org.springframework.context.annotation.Import
import org.springframework.data.repository.findByIdOrNull

@DataRedisTest
@Import(RedisConfig::class, JacksonAutoConfiguration::class, RedissonAutoConfigurationV2::class)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class WebsocketSessionRepositoryTest {
  private val log = LoggerFactory.getLogger(javaClass)

  @Autowired
  lateinit var websocketSessionRepository: WebsocketSessionRepository

  @Order(1)
  @Test
  fun 저장() {
    websocketSessionRepository.save(WebsocketSession.of("session01", "user01", "websocket1"))
    websocketSessionRepository.save(WebsocketSession.of("session02", "user01", "websocket2"))
  }

  @Order(2)
  @Test
  fun `id 기반 조회`() {
    log.debug("session1: {}", websocketSessionRepository.findByIdOrNull("session01"))
    log.debug("session2: {}", websocketSessionRepository.findByIdOrNull("session02"))
  }

  @Order(3)
  @Test
  fun `다른 필드를 통해 조회`() {
    log.debug("result: {}", websocketSessionRepository.findByServer("websocket1"))
  }

  @Order(4)
  @Test
  fun `업데이트`() {
    val session = websocketSessionRepository.findByIdOrNull("session01")!!
    session.server = "websocket3"
    websocketSessionRepository.save(session)
  }

  @Order(5)
  @Test
  fun 삭제() {
    websocketSessionRepository.deleteById("session01")
    websocketSessionRepository.deleteById("session02")
  }
}
