package com.ask.grafanaloki

import org.apache.commons.logging.LogFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.logging.LogLevel
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
class GrafanaLokiApplication

@RestController
class LogController {
  private val log = LogFactory.getLog(javaClass)

  @GetMapping("/log")
  fun log(message: String, level: LogLevel) {
    level.log(log, message)
  }

  @GetMapping("/exception")
  fun exception() {
    throw IllegalStateException("!!!! 예외발생")
  }
}

fun main(args: Array<String>) {
  runApplication<GrafanaLokiApplication>(*args)
}
