package com.ask.redisson.aop

import java.util.concurrent.TimeUnit

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class DistributedLock(
  val key: String,
  val waitTime: Long = 5,
  val leaseTime: Long = 3,
  val unit: TimeUnit = TimeUnit.SECONDS,
)
