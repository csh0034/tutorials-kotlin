package com.ask.cloudaws

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CloudAwsApplication

fun main(args: Array<String>) {
  runApplication<CloudAwsApplication>(*args)
}
