package com.ask.core.aop

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Aspect
class TransactionAspect(
  private val transactionDelegate: TransactionDelegate,
) {
  @Around(
    "execution(* com.ask..*Service.get*(..)) " +
      "|| execution(* com.ask..*Service.find*(..)) " +
      "|| execution(* com.ask..*Service.check*(..))"
  )
  fun applyReadOnlyTransaction(joinPoint: ProceedingJoinPoint): Any? {
    return transactionDelegate.readOnly(joinPoint)
  }

  @Around(
    "execution(* com.ask..*Service.*(..)) " +
      "&& !execution(* com.ask..*Service.get*(..)) " +
      "&& !execution(* com.ask..*Service.find*(..)) " +
      "&& !execution(* com.ask..*Service.check*(..))"
  )
  fun applyReadWriteTransaction(joinPoint: ProceedingJoinPoint): Any? {
    return transactionDelegate.readWrite(joinPoint)
  }
}

@Component
class TransactionDelegate {
  @Transactional(readOnly = true)
  fun readOnly(joinPoint: ProceedingJoinPoint): Any? {
    return joinPoint.proceed()
  }

  @Transactional
  fun readWrite(joinPoint: ProceedingJoinPoint): Any? {
    return joinPoint.proceed()
  }
}
