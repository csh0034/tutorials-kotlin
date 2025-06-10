package com.ask.archunit

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ArchunitApplication

fun main(args: Array<String>) {
  runApplication<ArchunitApplication>(*args)
}
