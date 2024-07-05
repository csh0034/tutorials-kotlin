package com.ask.virtualthread

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class TestController(
  private val jdbcTemplate: JdbcTemplate,
) {
  @GetMapping("/")
  fun getThreadName(): String {
    return Thread.currentThread().toString()
  }

  @GetMapping("/block")
  @Throws(InterruptedException::class)
  fun getBlockedResponse(): String {
    Thread.sleep(1000)
    return "OK"
  }

  @GetMapping("/query")
  fun queryAndReturn(): String {
    return jdbcTemplate.queryForList("select sleep(1);").toString()
  }
}

