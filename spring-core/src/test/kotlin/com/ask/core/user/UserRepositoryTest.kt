package com.ask.core.user

import com.ask.core.base.RepositoryTest
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.collections.shouldHaveSize

@RepositoryTest
class UserRepositoryTest(
  private val userRepository: UserRepository,
) : FunSpec({
  extensions(SpringExtension)

  test("role 을 통해 user 조회") {
    val result = userRepository.findUsers(Role.ADMIN)
    result shouldHaveSize 0
  }
})
