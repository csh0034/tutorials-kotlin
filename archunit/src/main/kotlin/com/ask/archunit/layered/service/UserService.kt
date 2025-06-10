package com.ask.archunit.layered.service

import com.ask.archunit.layered.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService (
  private val userRepository: UserRepository,
) {
  fun test() {
    test2()
  }

  private fun test2() {}
}
