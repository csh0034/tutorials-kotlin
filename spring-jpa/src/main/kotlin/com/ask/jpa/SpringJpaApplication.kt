package com.ask.jpa

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SpringJpaApplication

fun main(args: Array<String>) {
  runApplication<SpringJpaApplication>(*args)
}
