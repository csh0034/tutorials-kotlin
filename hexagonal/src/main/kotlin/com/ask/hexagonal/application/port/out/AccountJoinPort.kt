package com.ask.hexagonal.application.port.out

import com.ask.hexagonal.application.domain.model.Account
import com.ask.hexagonal.application.domain.model.AccountId

fun interface AccountJoinPort {
  fun saveAccount(account: Account): AccountId
}
