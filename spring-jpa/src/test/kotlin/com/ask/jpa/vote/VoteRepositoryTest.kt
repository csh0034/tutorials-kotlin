package com.ask.jpa.vote

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.repository.findByIdOrNull

@SpringBootTest
class VoteRepositoryTest {
  private val log = LoggerFactory.getLogger(javaClass)

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

  /**
   * @see <a href="https://docs.spring.io/spring-data/redis/reference/data-commons/repositories/query-keywords-reference.html">Repository query keywords</>
   */
  @DisplayName("query method 사용하므로 entity 조회후 delete 호출함, remove*")
  @Test
  fun removeByTitle() {
    log.debug("removeByTitle start")
    val result = voteRepository.removeByTitle("vote")
    assertThat(result).isOne()
    log.debug("removeByTitle end")
  }

  @DisplayName("query method 사용하므로 entity 조회후 delete 호출함, delete*")
  @Test
  fun deleteByTitle() {
    log.debug("deleteByTitle start")
    val result = voteRepository.deleteByTitle("vote")
    assertThat(result).isOne()
    log.debug("deleteByTitle end")
  }

  /**
   * @see <a href="https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html#jpa.modifying-queries">Modifying Queries
   * </>
   */
  @DisplayName("delete jpql 정상 동작하지만 외래키 제약조건 위반 예외 발생")
  @Test
  fun deleteAll() {
    log.debug("deleteAll start")
    assertThatExceptionOfType(DataIntegrityViolationException::class.java).isThrownBy {
      voteRepository.deleteAll("vote")
    }
    log.debug("deleteAll end")
  }
}
