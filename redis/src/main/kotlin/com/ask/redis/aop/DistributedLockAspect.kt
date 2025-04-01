package com.ask.redis.aop

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.redisson.api.RedissonClient
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.expression.spel.support.StandardEvaluationContext
import org.springframework.stereotype.Component

private const val LOCK_KEY_PREFIX = "LOCK:"

@Component
@Aspect
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
class DistributedLockAspect(
  private val redissonClient: RedissonClient,
) : AbstractAspect() {

  @Around("@annotation(annotation)")
  fun executeWithLock(joinPoint: ProceedingJoinPoint, annotation: DistributedLock): Any? {
    val evaluationContext = setParameters(joinPoint)
    val key = generateKey(annotation.key, evaluationContext)
    val lock = redissonClient.getLock(key)

    if (lock.tryLock(annotation.waitTime, annotation.leaseTime, annotation.unit)) {
      return runCatching { joinPoint.proceed() }
        .also { if (lock.isHeldByCurrentThread) lock.unlock() }
        .getOrThrow()
    }

    throw IllegalStateException("distributed lock error. key: $key")
  }

  private fun generateKey(key: String, evaluationContext: StandardEvaluationContext): String {
    val expression = EXPRESSION_PARSER.parseExpression(key)
    return "$LOCK_KEY_PREFIX:${expression.getValue(evaluationContext)}"
  }
}
