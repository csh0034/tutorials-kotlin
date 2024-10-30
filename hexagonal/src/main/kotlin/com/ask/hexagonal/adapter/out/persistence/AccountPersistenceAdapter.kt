package com.ask.hexagonal.adapter.out.persistence

import com.ask.base.PersistenceAdapter
import com.ask.hexagonal.application.domain.model.Account
import com.ask.hexagonal.application.domain.model.AccountId
import com.ask.hexagonal.application.port.out.AccountJoinPort
import com.ask.hexagonal.application.port.out.AccountLoadPort
import jakarta.persistence.EntityNotFoundException
import org.springframework.data.repository.findByIdOrNull

@PersistenceAdapter
class AccountPersistenceAdapter(
  private val accountJpaRepository: AccountJpaRepository,
  private val accountMapper: AccountMapper,
) : AccountJoinPort, AccountLoadPort {
  override fun saveAccount(account: Account): AccountId {
    val entity = AccountJpaEntity(name = account.name)
    return accountJpaRepository.save(entity).run { this.id!! }
  }

  override fun loadAccount(accountId: AccountId): Account {
    val account = accountJpaRepository.findByIdOrNull(accountId.value) ?: throw EntityNotFoundException("account not found")
    return accountMapper.mapToDomainEntity(account)
  }
}
