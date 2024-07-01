package com.ask.core.study

import jakarta.servlet.ServletException
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.stereotype.Controller
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RestControllerAdvice

@SpringBootTest
@AutoConfigureMockMvc
class RestControllerExceptionHandlerTest(
  @Autowired private val mockMvc: MockMvc,
) {
  @Test
  fun `RestController 에서 예외 발생시 RestControllerExceptionHandler 에서 처리 된다`() {
    mockMvc.get("/exception1")
      .andExpect {
        status { isOk() }
      }
  }

  @Test
  fun `Controller 에서 예외 발생시 RestControllerExceptionHandler 에서 처리 되지 않는다`() {
    assertThatExceptionOfType(ServletException::class.java).isThrownBy {
      mockMvc.get("/exception2")
    }.withMessageContaining("exception occurred, @Controller")
  }
}

// 만약 한 컨트롤러에 view/json 반환 메서드가 섞여있다면 ResponseBody::class 를 쓰면 된다.
@RestControllerAdvice(annotations = [RestController::class])
class RestControllerExceptionHandler {
  @ExceptionHandler
  fun handle(e: Exception) = mapOf("message" to e.message)
}

@RestController
class ExceptionController1 {
  @GetMapping("/exception1")
  fun exception(): String {
    throw IllegalStateException("exception occurred, @RestController")
  }
}

@Controller
class ExceptionController2 {
  @GetMapping("/exception2")
  fun exception(): String {
    throw IllegalStateException("exception occurred, @Controller")
  }
}
