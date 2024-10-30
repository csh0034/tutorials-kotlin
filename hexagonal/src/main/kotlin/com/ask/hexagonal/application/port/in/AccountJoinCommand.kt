package com.ask.hexagonal.application.port.`in`

import com.ask.base.validation.Validation.validate
import com.ask.hexagonal.application.domain.model.Account
import com.ask.hexagonal.application.domain.model.Name
import jakarta.validation.constraints.Size

data class AccountJoinCommand(
  @field:Size(min = 1, max = 10)
  val name: String,
) {
  init {
    validate(this)
  }

  fun toEntity() = Account.withoutId(Name(name))
}
