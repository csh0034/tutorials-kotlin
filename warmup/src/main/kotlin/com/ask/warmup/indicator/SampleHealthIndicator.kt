package com.ask.warmup.indicator

import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.stereotype.Component

@Component
class SampleHealthIndicator : HealthIndicator {
  override fun health(): Health {
    return Health.up().build()
  }
}
