package com.ask.core.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "core")
data class CoreProperties(
  val projectVersion: String,
)
