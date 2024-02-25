package com.ask.core

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SpringCoreApplication

fun main(args: Array<String>) {
  runApplication<SpringCoreApplication>(*args)
}
