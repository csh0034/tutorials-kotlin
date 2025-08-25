package com.ask.core.client

import org.assertj.core.api.Assertions.assertThatNoException
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@Disabled
@SpringBootTest
class GoogleClientTest {
  @Autowired
  lateinit var googleClient: GoogleClient

  @Test
  fun index() {
    assertThatNoException().isThrownBy { googleClient.index() }
  }

  @Test
  fun queryMap() {
    googleClient.queryMap(TestDto(10L, listOf("한글", "&&")))
  }

  @Test
  fun queryMap2() {
    googleClient.queryMap(mapOf("id" to "10", "names" to listOf("한글", "&&")))
  }
}
