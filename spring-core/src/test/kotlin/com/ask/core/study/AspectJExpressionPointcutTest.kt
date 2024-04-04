package com.ask.core.study

import com.ask.core.user.UserService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.aop.aspectj.AspectJExpressionPointcut

class AspectJExpressionPointcutTest {

  private val pointcut = AspectJExpressionPointcut()

  @Test
  fun matches() {
    pointcut.expression = "execution(* com.ask..*Service.*(..))"
    assertThat(pointcut.matches(UserService::class.java)).isTrue
  }
}
