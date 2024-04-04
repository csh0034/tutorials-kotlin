package com.ask.core.user

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class UserService(
  private val userRepository: UserRepository,
) {
  fun save(name: String, role: Role) {
    userRepository.save(User(name = name, role = role))
  }

  fun findByIdOrNull(id: String): User? {
    return userRepository.findByIdOrNull(id)
  }
}
