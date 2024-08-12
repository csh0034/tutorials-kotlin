package com.ask.core.config

import com.zaxxer.hikari.HikariDataSource
import com.zaxxer.hikari.HikariPoolMXBean
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class DbcpLogger(
  private val hikariDataSource: HikariDataSource,
) {
  private val log = LoggerFactory.getLogger(javaClass)

  @Scheduled(cron = "0/10 * * * * *")
  fun log() {
    log.info("[{}] {}", "spring-core", hikariDataSource.hikariPoolMXBean.stats())
  }

  private fun HikariPoolMXBean.stats() = DbcpInfo(
    totalConnections,
    activeConnections,
    idleConnections,
    threadsAwaitingConnection
  )
}

data class DbcpInfo(
  val total: Int,
  val active: Int,
  val idle: Int,
  val waiting: Int,
)
