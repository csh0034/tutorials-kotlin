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
}
