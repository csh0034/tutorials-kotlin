package com.ask.kubernetes

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class KubernetesApplication

fun main(args: Array<String>) {
  runApplication<KubernetesApplication>(*args)
}
