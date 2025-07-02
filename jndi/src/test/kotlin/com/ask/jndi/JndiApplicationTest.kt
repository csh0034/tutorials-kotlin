package com.ask.jndi

import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class JndiApplicationTest(
  @Autowired private val jdbcTemplate: JdbcTemplate,
) {
  private val log = LoggerFactory.getLogger(javaClass)

  @Test
  fun contextLoads() {
    val list = jdbcTemplate.queryForList("show databases")
    list.forEach { log.info("{}", it) }
  }
}
