package com.ask.resilience4j.config

import com.ask.resilience4j.client.BookClient
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpRequest
import org.springframework.http.HttpStatusCode
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import org.springframework.web.client.RestClient
import org.springframework.web.client.support.RestClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory

@Configuration
class RestClientConfig {
  @Bean
  fun googleClient(builder: RestClient.Builder): BookClient {
    return builder.toExchange("https://book.com")
  }

  private inline fun <reified T> RestClient.Builder.toExchange(baseUrl: String): T {
    val restClient = this.baseUrl(baseUrl)
      .defaultStatusHandler(HttpStatusCode::isError, RestClientErrorHandler())
      .requestInterceptor(RestClientLoggingInterceptor())
      .build()

    val adapter = RestClientAdapter.create(restClient)

    val factory = HttpServiceProxyFactory
      .builderFor(adapter)
      .build()

    return factory.createClient(T::class.java)
  }
}

private class RestClientErrorHandler : RestClient.ResponseSpec.ErrorHandler {
  private val log = LoggerFactory.getLogger(javaClass)

  override fun handle(request: HttpRequest, response: ClientHttpResponse) {
    log.warn("code: {}, message: {}", response.statusCode.value(), response.statusText)
  }
}

private class RestClientLoggingInterceptor : ClientHttpRequestInterceptor {
  private val log = LoggerFactory.getLogger(javaClass)

  override fun intercept(
    request: HttpRequest,
    body: ByteArray,
    execution: ClientHttpRequestExecution,
  ): ClientHttpResponse {
    log.info("request: [{}] {}", request.method, request.uri)
    return execution.execute(request, body)
  }
}
