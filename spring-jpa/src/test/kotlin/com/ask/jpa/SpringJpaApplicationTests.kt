package com.ask.jpa

import com.ask.jpa.user.Role
import com.ask.jpa.user.User
import com.ask.jpa.user.UserService
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class SpringJpaApplicationTests {
  private val log = LoggerFactory.getLogger(javaClass)

  @Autowired
  lateinit var userService: UserService

  @Test
  fun `bulk insert 테스트`() {
    val users = createUsers(3)
    log.debug("{}", users)
    userService.saveAll(users)
  }

  private fun createUsers(size: Int) = (1..size).map { User(name = "name-$it", role = Role.USER) }
}
