package com.ask.jpa.vote

import com.ask.jpa.vote.QVote.vote
import com.querydsl.jpa.impl.JPAQueryFactory
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
import org.springframework.transaction.support.TransactionTemplate

@SpringBootTest
class VoteRepositoryTest {
  private val log = LoggerFactory.getLogger(javaClass)

  @Autowired
  lateinit var voteRepository: VoteRepository

  @Autowired
  lateinit var queryFactory: JPAQueryFactory

  @Autowired
  lateinit var transactionTemplate: TransactionTemplate

  lateinit var voteId: String

  /**
   * hibernate.order_inserts true 일때 하나의 트랜잭션 안에 있다면 따로 save 하더라도 batch insert 처리됨
   */
  @BeforeEach
  fun setUp() {
    transactionTemplate.executeWithoutResult {
      val vote = Vote(title = "vote")
      vote.addVoteItem(VoteItem(vote = vote))
      vote.addVoteItem(VoteItem(vote = vote))
      voteRepository.save(vote)

      val vote2 = Vote(title = "vote2")
      vote2.addVoteItem(VoteItem(vote = vote2))
      vote2.addVoteItem(VoteItem(vote = vote2))
      voteRepository.save(vote2)

      voteId = vote.id!!
    }
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

  /**
   * default_batch_fetch_size 만큼 lazy 대상을 한번에 가져온다, 대상이 되지 않는 나머지는 null 로 채워짐
   */
  @Test
  fun default_batch_fetch_size() {
    log.debug("default_batch_fetch_size start")
    transactionTemplate.executeWithoutResult {
      val votes = queryFactory.selectFrom(vote)
        .fetch()

      votes.forEach { it.voteItems.size }
    }
    log.debug("default_batch_fetch_size end")
  }

  /**
   * in 절이 2의 제곱단위로 쿼리 생성하므로 a, b, c, c 로 조회됨
   */
  @Test
  fun `query in_clause_parameter_padding`() {
    log.debug("query.in_clause_parameter_padding start")
    queryFactory.selectFrom(vote)
      .where(vote.id.`in`("a", "b", "c"))
      .fetch()
    log.debug("query.in_clause_parameter_padding end")
  }
}
