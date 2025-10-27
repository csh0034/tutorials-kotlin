package com.ask.jpa.member

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface MemberRepository : JpaRepository<Member, String> {
  @Query("select decrypt_with_key(name) from Member")
  fun findWithCustomFunction(): List<String>
}
