package com.ask.jpa.vote

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull

@SpringBootTest
class VoteRepositoryTest {
  @Autowired
  lateinit var voteRepository: VoteRepository

  lateinit var voteId: String

  @BeforeEach
  fun setUp() {
    val vote = Vote(title = "vote")
    vote.voteItems.add(VoteItem(vote = vote))
    vote.voteItems.add(VoteItem(vote = vote))
    voteRepository.save(vote)

    voteId = vote.id!!
  }

  @AfterEach
  fun tearDown() {
    voteRepository.deleteAll()
  }

  @Test
  fun findByIdOrNull() {
    val vote = voteRepository.findByIdOrNull(voteId)
    assertThat(vote).isNotNull
  }

  @Test
  fun findFetchById() {
    val vote = voteRepository.getWithVoteItems(voteId)
    assertThat(vote).isNotNull
  }
}
