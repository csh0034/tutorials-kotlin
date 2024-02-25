package com.ask.core.base

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockHttpServletRequestDsl
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActionsDsl
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
@TestEnvironment
@AutoConfigureMockMvc
abstract class AbstractIntegrationTest {
  @Autowired
  lateinit var mockMvc: MockMvc

  @Autowired
  lateinit var objectMapper: ObjectMapper

  fun MockHttpServletRequestDsl.jsonContent(value: Any) {
    content = objectMapper.writeValueAsString(value)
    contentType = MediaType.APPLICATION_JSON
  }
}

inline fun <reified T> ResultActionsDsl.toObject(): T {
  return jacksonObjectMapper().readValue<T>(this.andReturn().response.contentAsString)
}
