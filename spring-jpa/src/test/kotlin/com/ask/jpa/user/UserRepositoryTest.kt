package com.ask.jpa.user

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

@SpringBootTest
class UserRepositoryTest {
  @Autowired
  lateinit var userRepository: UserRepository

  lateinit var userId: String

  @BeforeEach
  fun setUp() {
    val user = userRepository.save(User(name = "ask", role = Role.USER))
    userId = user.id!!
  }

  @Disabled
  @Test
  fun `update 시 db 원자적으로 처리하여 동시성 방지 `() {
    val executor = Executors.newFixedThreadPool(100)
    val tryCount = 10000
    val latch = CountDownLatch(tryCount)

    (1..tryCount).forEach { _ ->
      executor.run {
        userRepository.updateIncreaseCount(userId)
        latch.countDown()
      }
    }

    val user = userRepository.findByIdOrNull(userId)!!
    assertThat(user.count).isEqualTo(tryCount)
  }
}
