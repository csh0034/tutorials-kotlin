package com.ask.core.web

import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

@RestController
@RequestMapping("/param")
class ParameterController {
  private val log = LoggerFactory.getLogger(javaClass)

  @PostMapping("/instant")
  fun instant(@RequestParam time: Instant, @RequestBody body: InstantDto) {
    log.info("time: {}, body: {}", time, body)
  }
}

data class InstantDto(
  val time: Instant,
)
