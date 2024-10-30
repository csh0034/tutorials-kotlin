package com.ask.hexagonal

import com.ask.hexagonal.adapter.`in`.web.AccountJoinRequest
import com.ask.hexagonal.adapter.`in`.web.AccountJoinResponse
import com.ask.hexagonal.application.domain.model.AccountId
import com.ask.hexagonal.application.domain.model.Name
import com.ask.hexagonal.application.port.out.AccountLoadPort
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockHttpServletRequestDsl
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActionsDsl
import org.springframework.test.web.servlet.post
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class HexagonalApplicationTest(
  @Autowired private val mockMvc: MockMvc,
  @Autowired private val accountLoadPort: AccountLoadPort,
) {
  @Test
  fun join() {
    // given
    val request = AccountJoinRequest("ask")

    // when
    val result = mockMvc.post("/accounts") {
      jsonContent(request)
    }

    // then
    result.andExpect {
      status { isOk() }
    }

    val response = result.toObject<AccountJoinResponse>()
    val account = accountLoadPort.loadAccount(response.id)

    assertThat(account.name).isEqualTo(Name("ask"))
  }
}


fun MockHttpServletRequestDsl.jsonContent(value: Any) {
  content = jacksonObjectMapper().writeValueAsString(value)
  contentType = MediaType.APPLICATION_JSON
}

inline fun <reified T> ResultActionsDsl.toObject(): T {
  return jacksonObjectMapper().readValue<T>(this.andReturn().response.contentAsString)
}
