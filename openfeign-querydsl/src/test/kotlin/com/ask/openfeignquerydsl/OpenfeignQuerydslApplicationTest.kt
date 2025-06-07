package com.ask.openfeignquerydsl

import com.ask.openfeignquerydsl.user.model.Name
import com.ask.openfeignquerydsl.user.model.Role
import com.ask.openfeignquerydsl.user.model.User
import com.ask.openfeignquerydsl.user.repository.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class OpenfeignQuerydslApplicationTest {
  @Autowired
  lateinit var userRepository: UserRepository

  @Test
  fun save() {
    val user = User(name = Name("ASk"), role = Role.USER, count = 10)
    userRepository.save(user)
  }

  @Test
  fun find() {
    val user = userRepository.find("user01")
    assertThat(user).isNull()
  }

  @Test
  fun findUsers() {
    userRepository.findUsers(Role.USER)
  }
}
