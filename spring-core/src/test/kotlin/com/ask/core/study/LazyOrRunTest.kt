package com.ask.core.study

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.springframework.core.io.ClassPathResource
import java.nio.charset.Charset

class LazyOrRunTest : FunSpec({
  /**
   * 초기화에 실패한걸 즉시 인지해야 할 경우
   */
  test("run 사용") {
    var init = false

    val txt = run {
      init = true
      val resource = ClassPathResource("test.txt")
      resource.getContentAsString(Charset.defaultCharset())
    }

    init shouldBe true
    txt shouldBe "test file\n"
  }

  /**
   * 초기화에 실패한걸 사용시점에 인지해야 하거나 실제 사용 시점에 초기화 하고싶을때
   */
  test("lazy 사용") {
    var init = false

    val txt by lazy {
      init = true
      val resource = ClassPathResource("test.txt")
      resource.getContentAsString(Charset.defaultCharset())
    }

    init shouldBe false
    txt shouldBe "test file\n"
    init shouldBe true
  }
})
