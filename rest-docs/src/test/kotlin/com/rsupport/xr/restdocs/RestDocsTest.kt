package com.rsupport.xr.restdocs

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.restdocs.RestDocsMockMvcConfigurationCustomizer
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.restdocs.headers.HeaderDocumentation.headerWithName
import org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.queryParameters
import org.springframework.restdocs.snippet.Snippet
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MockMvcResultHandlersDsl
import org.springframework.test.web.servlet.get
import java.util.UUID

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
class RestDocsTest(
  @Autowired private val mockMvc: MockMvc,
) {
  @Test
  fun test() {
    mockMvc.get("/test") {
      header("UUID", UUID.randomUUID().toString())
      queryParam("name", "ask")
    }.andExpectAll {
      status { isOk() }
      jsonPath("$.name") { value("ask") }
    }.andDo {
      print()
      document(
        "{class-name}/{method-name}",
        requestHeaders(
          headerWithName("UUID").description("UUID").optional()
        ),
        queryParameters(
          parameterWithName("name").description("이름")
        ),
        responseFields(
          fieldWithPath("name").description("이름"),
          fieldWithPath("timestamp").description("utc timestamp")
        ),
      )
    }
  }

  @TestConfiguration
  class RestDocsConfiguration {
    @Bean
    fun restDocsMockMvcConfigurationCustomizer() = RestDocsMockMvcConfigurationCustomizer {
      it.operationPreprocessors()
        .withRequestDefaults(prettyPrint())
        .withResponseDefaults(prettyPrint())
    }
  }
}

fun MockMvcResultHandlersDsl.document(identifier: String, vararg snippets: Snippet) {
  handle(MockMvcRestDocumentation.document(identifier, *snippets))
}
