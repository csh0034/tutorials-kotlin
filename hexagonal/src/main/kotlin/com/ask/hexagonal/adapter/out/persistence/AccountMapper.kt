package com.ask.hexagonal.adapter.out.persistence

import com.ask.hexagonal.application.domain.model.Account
import org.springframework.stereotype.Component

@Component
class AccountMapper {
  fun mapToDomainEntity(account: AccountJpaEntity): Account {
    return Account.withId(account.id!!, account.name)
  }
}
