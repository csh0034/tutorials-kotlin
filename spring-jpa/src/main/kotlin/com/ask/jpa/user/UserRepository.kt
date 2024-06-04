package com.ask.jpa.user

import com.ask.jpa.user.QUser.user
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, String>, UserRepositoryCustom

interface UserRepositoryCustom {
  fun findUsers(role: Role): MutableList<User>
}

class UserRepositoryCustomImpl(
  private val queryFactory: JPAQueryFactory,
) : UserRepositoryCustom {
  override fun findUsers(role: Role): MutableList<User> {
    return queryFactory.selectFrom(user)
      .where(user.role.eq(role))
      .fetch()
  }
}
