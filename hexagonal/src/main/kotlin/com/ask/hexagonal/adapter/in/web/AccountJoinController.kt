package com.ask.hexagonal.adapter.`in`.web

import com.ask.base.WebAdapter
import com.ask.hexagonal.application.domain.model.AccountId
import com.ask.hexagonal.application.port.`in`.AccountJoinCommand
import com.ask.hexagonal.application.port.`in`.AccountJoinUseCase
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@WebAdapter
@RestController
internal class AccountJoinController(
  private val accountJoinUseCase: AccountJoinUseCase,
) {
  @PostMapping(path = ["/accounts"])
  fun join(@RequestBody request: AccountJoinRequest): AccountJoinResponse {
    return accountJoinUseCase.join(request.toCommand()).run { AccountJoinResponse(this) }
  }
}

data class AccountJoinRequest(
  val name: String,
) {
  fun toCommand() = AccountJoinCommand(name)
}

data class AccountJoinResponse(
  val id: AccountId,
)

