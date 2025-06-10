package com.ask.archunit.layered.web

import com.ask.archunit.layered.service.UserService
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController(
  private val userService: UserService,
)
