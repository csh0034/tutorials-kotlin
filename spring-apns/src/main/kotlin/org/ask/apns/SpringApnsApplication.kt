package org.ask.apns

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SpringApnsApplication

fun main(args: Array<String>) {
  runApplication<SpringApnsApplication>(*args)
}
