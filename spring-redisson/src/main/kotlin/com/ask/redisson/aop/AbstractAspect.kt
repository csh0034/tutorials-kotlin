package com.ask.redisson.aop

import org.aspectj.lang.JoinPoint
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.expression.spel.support.StandardEvaluationContext

abstract class AbstractAspect {
  fun setParameters(joinPoint: JoinPoint): StandardEvaluationContext {
    val signature = joinPoint.signature as MethodSignature
    val parameterValues = joinPoint.args

    val evaluationContext = StandardEvaluationContext()
    for ((index, value) in signature.parameterNames.withIndex()) {
      evaluationContext.setVariable(value, parameterValues[index])
    }

    return evaluationContext
  }

  companion object {
    val EXPRESSION_PARSER = SpelExpressionParser()
  }
}
