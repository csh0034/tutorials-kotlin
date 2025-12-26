package com.ask.redis.aop

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.redisson.api.RedissonClient
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.expression.spel.support.StandardEvaluationContext
import org.springframework.stereotype.Component
import org.springframework.util.DigestUtils

private const val LOCK_KEY_PREFIX = "lock"

@Component
@Aspect
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
class DistributedLockAspect(
  private val redissonClient: RedissonClient,
) : AbstractAspect() {

  @Around("@annotation(annotation)")
  fun executeWithLock(joinPoint: ProceedingJoinPoint, annotation: DistributedLock): Any? {
    val evaluationContext = createEvaluationContext(joinPoint)
    val key = evaluationContext.generateKey(annotation.key)
    val lock = redissonClient.getLock(key)

    if (lock.tryLock(annotation.waitTime, annotation.leaseTime, annotation.unit)) {
      return runCatching { joinPoint.proceed() }
        .also { if (lock.isHeldByCurrentThread) lock.unlock() }
        .getOrThrow()
    }

    throw IllegalStateException("distributed lock failed. key: $key")
  }

  private fun StandardEvaluationContext.generateKey(key: String): String {
    val expression = EXPRESSION_PARSER.parseExpression(key)
    val rawKey = requireNotNull(expression.getValue(this, String::class.java))
    return "$LOCK_KEY_PREFIX:${rawKey.md5Hex()}"
  }

  private fun String.md5Hex() = DigestUtils.md5DigestAsHex(this.toByteArray())
}
