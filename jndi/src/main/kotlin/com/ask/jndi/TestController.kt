package com.ask.jndi

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class TestController(
  private val jdbcTemplate: JdbcTemplate,
) {
  @GetMapping("/")
  fun index(sql: String = "show databases"): Any {
    return jdbcTemplate.queryForList(sql)
  }
}
