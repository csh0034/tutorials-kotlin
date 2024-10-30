package com.ask.hexagonal.application.port.`in`

import com.ask.hexagonal.application.domain.model.AccountId

fun interface AccountJoinUseCase {
  fun join(command: AccountJoinCommand): AccountId
}
