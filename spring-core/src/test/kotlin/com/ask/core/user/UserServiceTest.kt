package com.ask.core.user

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class UserServiceTest {
  @Autowired
  lateinit var userService: UserService

  @Test
  fun save() {
    userService.save("name", Role.USER)
  }

  @Test
  fun findByIdOrNull() {
    val user = userService.findByIdOrNull("user01")
    assertThat(user).isNotNull
  }
}
