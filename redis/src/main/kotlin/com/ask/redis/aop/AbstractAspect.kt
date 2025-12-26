package com.ask.redis.aop

import org.aspectj.lang.JoinPoint
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.expression.spel.support.StandardEvaluationContext
import kotlin.reflect.jvm.javaMethod

abstract class AbstractAspect {
  fun createEvaluationContext(joinPoint: JoinPoint): StandardEvaluationContext {
    val signature = joinPoint.signature as MethodSignature
    val parameterValues = joinPoint.args

    val evaluationContext = StandardEvaluationContext().apply {
      setVariable("sortedToString", ::sortedToString.javaMethod)
    }

    for ((index, value) in signature.parameterNames.withIndex()) {
      evaluationContext.setVariable(value, parameterValues[index])
    }

    return evaluationContext
  }

  companion object {
    val EXPRESSION_PARSER = SpelExpressionParser()
  }
}

private fun sortedToString(ids: List<*>) = ids.sortedBy { it.toString() }.toString()
