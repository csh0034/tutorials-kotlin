package com.ask.core.fixture

import org.assertj.core.api.Assertions.assertThatIllegalArgumentException
import org.assertj.core.api.Assertions.assertThatNoException
import org.junit.jupiter.api.Test

/**
 * @see <a href="https://tech.kakaopay.com/post/katfun-joy-kotlin">kakaopay tech blog</>
 */
class FixtureTest {
  @Test
  fun `나이가 18세 미만이면 실패한다`() {
    val invalidAge = 17

    assertThatIllegalArgumentException().isThrownBy {
      validUserInfo.copy(age = invalidAge)
    }
  }

  @Test
  fun `나이가 18세 이상이면 성공한다`() {
    val validAge = 18

    assertThatNoException().isThrownBy {
      validUserInfo.copy(age = validAge)
    }
  }

  private val validUserInfo = UserInfo(
    name = "ask",
    age = 30,
  )
}

private data class UserInfo(
  val name: String,
  val age: Int,
) {
  init {
    require(age >= 18)
  }
}
