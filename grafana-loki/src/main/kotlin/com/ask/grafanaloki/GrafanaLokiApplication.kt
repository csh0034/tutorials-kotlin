package com.ask.grafanaloki

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GrafanaLokiApplication

fun main(args: Array<String>) {
  runApplication<GrafanaLokiApplication>(*args)
}
