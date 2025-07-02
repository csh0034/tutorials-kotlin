package com.ask.jndi

import org.apache.commons.dbcp2.BasicDataSource
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import javax.sql.DataSource

@Component
class DbcpLogger(
  private val dataSource: DataSource,
) {
  private val log = LoggerFactory.getLogger(javaClass)

  @Scheduled(cron = "0/10 * * * * *")
  fun log() {
    if (dataSource !is BasicDataSource) return

    val info = DataSourceInfo.from(dataSource)
    log.info("[{}] {}", "jndi", info)
  }
}

data class DataSourceInfo(
  val total: Int,
  val numActive: Int,
  val numIdle: Int,
  val maxTotal: Int,
  val initialSize: Int,
  val maxIdle: Int,
  val minIdle: Int,
  val timeBetweenEvictionRunsMillis: Long,
) {
  companion object {
    fun from(dataSource: BasicDataSource) = DataSourceInfo(
      dataSource.numActive + dataSource.numIdle,
      dataSource.numActive,
      dataSource.numIdle,
      dataSource.maxTotal,
      dataSource.maxIdle,
      dataSource.minIdle,
      dataSource.initialSize,
      dataSource.durationBetweenEvictionRuns.toMillis(),
    )
  }
}
