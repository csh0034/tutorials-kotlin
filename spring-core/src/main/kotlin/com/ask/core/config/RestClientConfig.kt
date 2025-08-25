package com.ask.core.config

import com.ask.core.client.GoogleClient
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.MethodParameter
import org.springframework.http.HttpRequest
import org.springframework.http.HttpStatusCode
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import org.springframework.web.client.RestClient
import org.springframework.web.client.support.RestClientAdapter
import org.springframework.web.service.invoker.HttpRequestValues
import org.springframework.web.service.invoker.HttpServiceArgumentResolver
import org.springframework.web.service.invoker.HttpServiceProxyFactory
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

@Configuration
class RestClientConfig {
  @Bean
  fun googleClient(builder: RestClient.Builder): GoogleClient {
    return builder.toExchange("https://google.com")
  }

  private inline fun <reified T> RestClient.Builder.toExchange(baseUrl: String): T {
    val restClient = this.baseUrl(baseUrl)
      .defaultStatusHandler(HttpStatusCode::isError, RestClientErrorHandler())
      .requestInterceptor(RestClientLoggingInterceptor())
      .build()

    val adapter = RestClientAdapter.create(restClient)

    val factory = HttpServiceProxyFactory
      .builderFor(adapter)
      .customArgumentResolver(QueryMapHttpServiceArgumentResolver())
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

  override fun intercept(request: HttpRequest, body: ByteArray, execution: ClientHttpRequestExecution): ClientHttpResponse {
    log.info("request: [{}] {}", request.method, request.uri)
    return execution.execute(request, body)
  }
}

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class QueryMap

private class QueryMapHttpServiceArgumentResolver : HttpServiceArgumentResolver {
  override fun resolve(argument: Any?, parameter: MethodParameter, requestValues: HttpRequestValues.Builder): Boolean {
    if (!parameter.hasParameterAnnotation(QueryMap::class.java)) return false
    requireNotNull(argument) { "argument cannot be null" }

    for (entry in argument.toMap()) {
      for (value in entry.value) {
        requestValues.addRequestParameter(entry.key, value)
      }
    }

    return true
  }

  private fun Any.toMap(): Map<String, List<String?>> {
    return (this::class as KClass<Any>).memberProperties.associate { prop ->
      prop.name to this.toValue(prop)
    }
  }

  private fun Any.toValue(prop: KProperty1<Any, *>): List<String?> {
    val values = mutableListOf<String?>()
    val value = prop.get(this)

    when (value) {
      is Iterable<*> -> {
        val iter = value.iterator()
        while (iter.hasNext()) {
          val nextObject = iter.next()
          values.add(nextObject.toString())
        }
      }

      is Array<*> -> {
        for (item in value) {
          values.add(item.toString())
        }
      }

      else -> {
        values.add(value.toString())
      }
    }

    return values
  }
}
