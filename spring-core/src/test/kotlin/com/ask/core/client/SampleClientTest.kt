package com.ask.core.client

import io.kotest.core.spec.style.FunSpec
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class SampleClientTest(
  val sampleClient: SampleClient,
) : FunSpec({
  test("get") {
    sampleClient.get(SampleDto(1, listOf("a", "b")))
  }
})
