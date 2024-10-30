package com.ask.hexagonal.application.domain.service

import com.ask.base.UseCase
import com.ask.hexagonal.application.domain.model.AccountId
import com.ask.hexagonal.application.port.`in`.AccountJoinCommand
import com.ask.hexagonal.application.port.`in`.AccountJoinUseCase
import com.ask.hexagonal.application.port.out.AccountJoinPort
import jakarta.transaction.Transactional

@UseCase
@Transactional
class AccountJoinService(
  private val accountJoinPort: AccountJoinPort,
): AccountJoinUseCase {
  override fun join(command: AccountJoinCommand): AccountId {
    return accountJoinPort.saveAccount(command.toEntity())
  }
}
