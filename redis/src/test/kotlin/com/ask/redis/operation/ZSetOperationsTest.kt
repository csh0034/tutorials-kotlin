package com.ask.redis.operation

import jakarta.annotation.Resource
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.core.ZSetOperations

private const val RANK = "rank"

/**
 * @see org.springframework.data.redis.core.ZSetOperationsEditor
 */
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ZSetOperationsTest {
  @Resource(name = "redisTemplate")
  lateinit var zSetOperations: ZSetOperations<Any, Any>

  @Order(1)
  @Test
  fun `ZADD`() {
    zSetOperations.add(RANK, "a", 1.0)
    zSetOperations.add(RANK, "b", 2.0)
    zSetOperations.add(RANK, "c", 3.0)
  }

  @Order(2)
  @Test
  fun `ZRANGE`() {
    assertThat(zSetOperations.range(RANK, 0, -1)).containsExactly("a", "b", "c")
  }

  @Order(3)
  @Test
  fun `ZRANGE WITHSCORES`() {
    val result = zSetOperations.rangeWithScores(RANK, 0, -1)
    assertThat(result?.map { it.score }).isEqualTo(listOf(1.0, 2.0, 3.0))
  }

  @Order(4)
  @Test
  fun `ZRANK`() {
    assertThat(zSetOperations.rank(RANK, "a")).isEqualTo(0)
    assertThat(zSetOperations.rank(RANK, "b")).isEqualTo(1)
    assertThat(zSetOperations.rank(RANK, "c")).isEqualTo(2)
  }

  @Order(5)
  @Test
  fun `ZREVRANK`() {
    assertThat(zSetOperations.reverseRank(RANK, "a")).isEqualTo(2)
    assertThat(zSetOperations.reverseRank(RANK, "b")).isEqualTo(1)
    assertThat(zSetOperations.reverseRank(RANK, "c")).isEqualTo(0)
  }

  @Order(6)
  @Test
  fun `ZINCRBY`() {
    zSetOperations.incrementScore(RANK, "a", 3.0)
    assertThat(zSetOperations.rank(RANK, "a")).isEqualTo(2)
  }

  @AfterAll
  fun afterAll() {
    zSetOperations.operations.delete(RANK)
  }
}
