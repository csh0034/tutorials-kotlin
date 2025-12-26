package com.ask.core.study

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.expression.spel.support.StandardEvaluationContext
import kotlin.reflect.jvm.javaMethod

class SpelExpressionParserTest : FunSpec({
  test("객체 프로퍼티 접근") {
    val expressionParser = SpelExpressionParser()
    val expression = expressionParser.parseExpression("#dto.first + '-' + #dto.last")

    val evaluationContext = StandardEvaluationContext()
    evaluationContext.setVariable("dto", TestDto("id01", 50))

    val value = expression.getValue(evaluationContext, String::class.java)
    value shouldBe "id01-50"
  }

  test("function 호출") {
    val expressionParser = SpelExpressionParser()
    val expression = expressionParser.parseExpression("#sortedToString(#memberIds)")

    val evaluationContext = StandardEvaluationContext()
    evaluationContext.setVariable("memberIds", listOf("C", "B", "A"))
    evaluationContext.setVariable("sortedToString", ::sortedToString.javaMethod) // registerFunction 사용 가능하지만 nullable 하지 않음

    val value = expression.getValue(evaluationContext, String::class.java)
    value shouldBe "[A, B, C]"
  }
})

private fun sortedToString(ids: List<*>) = ids.sortedBy { it.toString() }.toString()

private data class TestDto(
  val first: String,
  val last: Int,
)
