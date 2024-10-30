package com.ask.hexagonal.adapter.out.persistence

import org.springframework.data.jpa.repository.JpaRepository

interface AccountJpaRepository : JpaRepository<AccountJpaEntity, String>
