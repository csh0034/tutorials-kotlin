package com.ask.jpa.user

import com.ask.jpa.user.QUser.user
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional

interface UserRepository : JpaRepository<User, String>, UserRepositoryCustom {

  @Transactional
  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("update User set count = count + 1 where id = :userId")
  fun updateIncreaseCount(userId: String)
}

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
