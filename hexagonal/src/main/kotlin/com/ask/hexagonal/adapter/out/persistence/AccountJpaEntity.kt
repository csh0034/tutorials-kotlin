package com.ask.hexagonal.adapter.out.persistence

import com.ask.base.PrefixUuid
import com.ask.hexagonal.application.domain.model.AccountId
import com.ask.hexagonal.application.domain.model.Name
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "account")
class AccountJpaEntity(
  @Id
  @PrefixUuid(AccountId.ID_PREFIX)
  @Column(length = 50)
  var id: AccountId? = null,

  @Column(length = 10)
  val name: Name,
)
