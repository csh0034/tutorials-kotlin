package com.ask.core.web

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 예외 발생하더라도 xml 로 처리
 * @see <a href="https://docs.spring.io/spring-boot/how-to/spring-mvc.html#howto.spring-mvc.write-xml-rest-service">Write an XML REST Service</a>
 */
@RestController
class XmlController {
  @RequestMapping("/xml", produces = [MediaType.TEXT_XML_VALUE])
  fun xml(): XmlResponse {
    return XmlResponse.ok()
  }

  @ExceptionHandler(produces = [MediaType.TEXT_XML_VALUE])
  fun handleException(exception: Exception): XmlResponse {
    return XmlResponse.error(exception.message)
  }
}

@JacksonXmlRootElement(localName = "RESULT")
@JsonInclude(JsonInclude.Include.NON_NULL)
data class XmlResponse(
  @JacksonXmlProperty(localName = "RETCODE")
  val code: String,

  @JacksonXmlProperty(localName = "MESSAGE")
  val message: String?,
) {
  companion object {
    const val OK = "100"
    const val ERROR = "999"

    fun ok() = XmlResponse(OK, null)
    fun error(message: String?) = XmlResponse(ERROR, message)
  }
}
