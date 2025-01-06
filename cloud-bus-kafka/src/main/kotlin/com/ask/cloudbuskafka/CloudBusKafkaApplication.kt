package com.ask.cloudbuskafka

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CloudBusKafkaApplication

fun main(args: Array<String>) {
  runApplication<CloudBusKafkaApplication>(*args)
}
