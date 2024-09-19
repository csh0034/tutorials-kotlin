package com.ask.jpa.user

import com.ask.jpa.user.QUser.user
import com.querydsl.core.annotations.QueryProjection
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.impl.JPAQueryFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

@SpringBootTest
class UserRepositoryTest {
  private val log = LoggerFactory.getLogger(javaClass)

  @Autowired
  lateinit var userRepository: UserRepository

  @Autowired
  lateinit var queryFactory: JPAQueryFactory

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

  /**
   * Projections.fields 를 사용할 경우
   * - Expressions.asString("constant...").as("name") 와 같이 as 가 추가되어야함
   * - @QueryProjection 사용시 as 는 필요 없음
   */
  @Test
  fun `constant 사용 검증`() {
    val users = queryFactory.select(
      QUserDto(
        user.id,
        Expressions.asString("constant...").`as`("name")
      )
    )
      .from(user)
      .fetch()

    users.forEach { log.info("it: $it") }
  }
}

data class UserDto @QueryProjection constructor(
  val id: String,
  val name: String,
)
