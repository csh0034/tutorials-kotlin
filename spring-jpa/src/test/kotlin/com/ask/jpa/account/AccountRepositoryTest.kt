package com.ask.jpa.account

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
class AccountRepositoryTest {
  @Autowired
  lateinit var accountRepository: AccountRepository

  @Test
  fun `account 생성`() {
    val account = Account.create()
    accountRepository.save(account)
    accountRepository.flush()
  }

  @Test
  fun `account 조회`() {
    val newAccount = Account.create()
    accountRepository.save(newAccount)

    val findAccount = accountRepository.findByIdOrNull(newAccount.id.value)
    assertThat(findAccount).isNotNull
  }
}
