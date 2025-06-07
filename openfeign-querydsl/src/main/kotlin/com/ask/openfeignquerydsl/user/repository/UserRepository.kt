package com.ask.openfeignquerydsl.user.repository

import com.ask.openfeignquerydsl.user.dto.QUserDto
import com.ask.openfeignquerydsl.user.dto.UserDto
import com.ask.openfeignquerydsl.user.model.QUser.Companion.user
import com.ask.openfeignquerydsl.user.model.Role
import com.ask.openfeignquerydsl.user.model.User
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, String>, UserRepositoryCustom

interface UserRepositoryCustom {
  fun find(id: String): UserDto?
  fun findUsers(role: Role): MutableList<User>
}

open class UserRepositoryCustomImpl(
  private val queryFactory: JPAQueryFactory,
) : UserRepositoryCustom {
  override fun find(id: String): UserDto? {
    return queryFactory.select(
      QUserDto(
        user.id,
        user.name
      )
    )
      .from(user)
      .where(user.id.eq(id))
      .fetchOne()
  }

  override fun findUsers(role: Role): MutableList<User> {
    return queryFactory.selectFrom(user)
      .where(user.role.eq(role))
      .fetch()
  }
}
