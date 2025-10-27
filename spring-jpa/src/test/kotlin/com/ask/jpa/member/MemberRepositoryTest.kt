package com.ask.jpa.member

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull

@SpringBootTest
class MemberRepositoryTest {
  @Autowired
  lateinit var memberRepository: MemberRepository

  @Test
  fun `member 조회`() {
    val member = Member(email = "email@test.com", phone = "010-1234-5678", name = "Test User2")
    memberRepository.save(member)

    val findMember = memberRepository.findByIdOrNull(member.id!!)
    assertThat(findMember).isNotNull
  }

  @Test
  fun `custom function 사용`() {
    val emails = memberRepository.findWithCustomFunction()
    assertThat(emails).hasSize(0)
  }
}
