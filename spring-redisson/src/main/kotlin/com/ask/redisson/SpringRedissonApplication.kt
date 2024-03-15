package com.ask.redisson

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SpringRedissonApplication

fun main(args: Array<String>) {
  runApplication<SpringRedissonApplication>(*args)
}
