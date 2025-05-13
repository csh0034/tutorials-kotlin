package com.ask.core.user

import com.ask.core.base.RepositoryTest
import com.ask.core.config.CacheConfig
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.collections.shouldHaveSize
import org.springframework.boot.autoconfigure.cache.CacheType
import org.springframework.boot.test.autoconfigure.core.AutoConfigureCache
import org.springframework.context.annotation.Import

@RepositoryTest
@AutoConfigureCache(cacheProvider = CacheType.CAFFEINE)
@Import(CacheConfig::class)
class UserRepositoryTest(
  private val userRepository: UserRepository,
) : FunSpec({
  extensions(SpringExtension)

  test("role 을 통해 user 조회") {
    val result = userRepository.findUsers(Role.ADMIN)
    userRepository.findUsers(Role.ADMIN)
    userRepository.findUsers(Role.ADMIN)
    result shouldHaveSize 0
  }
})
