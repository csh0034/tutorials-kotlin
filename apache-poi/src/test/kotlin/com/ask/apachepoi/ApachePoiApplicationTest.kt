package com.ask.apachepoi

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldNotBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext

@SpringBootTest
class ApachePoiApplicationTest(
  private val applicationConContext: ApplicationContext,
) : FunSpec({
  test("contextLoad") {
    applicationConContext shouldNotBe null
  }
})

