package com.ask.core.config

import com.ask.core.client.GoogleClient
import org.slf4j.LoggerFactory
import org.springframework.boot.web.client.RestClientCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpRequest
import org.springframework.http.HttpStatusCode
import org.springframework.http.client.ClientHttpResponse
import org.springframework.web.client.RestClient
import org.springframework.web.client.support.RestClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory

@Configuration
class RestClientConfig {
  @Bean
  fun googleClient(builder: RestClient.Builder): GoogleClient {
    val restClient = builder.build()
    val adapter = RestClientAdapter.create(restClient)
    val factory = HttpServiceProxyFactory.builderFor(adapter).build()
    return factory.createClient(GoogleClient::class.java)
  }

  @Bean
  fun restClientCustomizer() = RestClientCustomizer { customizer ->
    customizer.defaultStatusHandler(HttpStatusCode::isError, RestClientErrorHandler())
  }
}

private class RestClientErrorHandler : RestClient.ResponseSpec.ErrorHandler {
  private val log = LoggerFactory.getLogger(javaClass)

  override fun handle(request: HttpRequest, response: ClientHttpResponse) {
    log.warn("code: {}, message: {}", response.statusCode.value(), response.statusText)
  }
}
