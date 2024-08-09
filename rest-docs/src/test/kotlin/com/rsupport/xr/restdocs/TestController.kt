package com.rsupport.xr.restdocs

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class TestController {
  @GetMapping("/test")
  fun test(name: String) = mapOf("name" to name, "timestamp" to System.currentTimeMillis().toString())
}
