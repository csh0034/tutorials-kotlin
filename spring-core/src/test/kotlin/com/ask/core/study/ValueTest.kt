package com.ask.core.study

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatIllegalArgumentException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import java.util.regex.Pattern

class ValueTest {
  @Test
  fun `email 형식이 아닌경우 예외 발생`() {
    assertThatIllegalArgumentException().isThrownBy {
      Email("asdf")
    }
  }

  @Test
  fun `email id 와 host 검증`() {
    val email = Email("test@naver.com")
    assertAll(
      { assertThat(email.value).isEqualTo("test@naver.com") },
      { assertThat(email.getId()).isEqualTo("test") },
      { assertThat(email.getHost()).isEqualTo("naver.com") },
    )
  }
}

@JvmInline
value class Email(val value: String) {
  init {
    if (!Pattern.matches("^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$", value)) {
      throw IllegalArgumentException("not match regex and email")
    }
  }

  fun getId(): String {
    return value.split("@")[0]
  }

  fun getHost(): String {
    return value.split("@")[1]
  }
}
