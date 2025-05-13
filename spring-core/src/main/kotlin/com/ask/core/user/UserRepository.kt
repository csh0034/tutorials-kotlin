package com.ask.core.user

import com.ask.core.config.Cache
import com.ask.core.user.QUser.user
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, String>, UserRepositoryCustom

interface UserRepositoryCustom {
  fun findUsers(role: Role): MutableList<User>
}

open class UserRepositoryCustomImpl(
  private val queryFactory: JPAQueryFactory,
) : UserRepositoryCustom {
  @Cacheable(Cache.USER)
  override fun findUsers(role: Role): MutableList<User> {
    return queryFactory.selectFrom(user)
      .where(user.role.eq(role))
      .fetch()
  }
}
