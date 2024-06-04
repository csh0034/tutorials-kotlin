package com.ask.jpa.user

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UserService(
  private val userRepository: UserRepository,
) {
  fun save(name: String, role: Role) {
    userRepository.save(User(name = name, role = role))
  }

  fun findByIdOrNull(id: String): User? {
    return userRepository.findByIdOrNull(id)
  }

  fun saveAll(users: List<User>): MutableList<User> = userRepository.saveAll(users)
}
