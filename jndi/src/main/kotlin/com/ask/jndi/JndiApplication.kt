package com.ask.jndi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class JndiApplication

fun main(args: Array<String>) {
  runApplication<JndiApplication>(*args)
}
