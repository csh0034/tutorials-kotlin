package com.ask.hexagonal.application.port.out

import com.ask.hexagonal.application.domain.model.Account
import com.ask.hexagonal.application.domain.model.AccountId

fun interface AccountLoadPort {
  fun loadAccount(accountId: AccountId): Account
}
