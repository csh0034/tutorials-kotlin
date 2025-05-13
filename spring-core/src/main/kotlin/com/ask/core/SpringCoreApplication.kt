package com.ask.core

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.context.support.beans
import org.springframework.web.servlet.function.router

@SpringBootApplication
@ConfigurationPropertiesScan
class SpringCoreApplication

val beans = beans {
  bean("word") { "Test" }
  bean(::route)
}

fun route(word: String) = router {
  "/route".nest {
    GET("") { _ -> ok().body(word) }
  }
}

fun main(args: Array<String>) {
  runApplication<SpringCoreApplication>(*args) {
    addInitializers(beans)
  }
}
