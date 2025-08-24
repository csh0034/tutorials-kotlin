package com.ask.core.study

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties

class ReflectionTest : FunSpec({
  test("get property") {
    val testClass: Any = TestClass("1", "2")

    (testClass::class as KClass<Any>).memberProperties
      .map { prop -> "${prop.name}: ${prop.get(testClass)}" }
      .toList() shouldBe listOf("a: 1", "b: 2")
  }
})

data class TestClass(
  val a: String,
  val b: String,
)
