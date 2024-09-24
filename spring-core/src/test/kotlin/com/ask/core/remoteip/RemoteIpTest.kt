package com.ask.core.remoteip

import jakarta.servlet.http.HttpServletRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.test.context.TestPropertySource
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController

private const val FOR_HEADER = "X-Forwarded-For"
private const val IP = "ip"

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(
  properties = [
    "server.tomcat.remoteip.remote-ip-header=$FOR_HEADER",
    "server.tomcat.remoteip.internal-proxies=\\\\d{1,3}\\\\.\\\\d{1,3}\\\\.\\\\d{1,3}\\\\.\\\\d{1,3}",
  ]
)
class RemoteIpTest {
  @Autowired
  lateinit var restTemplate: TestRestTemplate

  @Test
  fun `internal proxies 모두허용시 ip 변조 가능`() {
    // given
    val headers = HttpHeaders()
    headers.add(FOR_HEADER, "1.1.1.1, 2.2.2.2, 3.3.3.3")

    // when
    val responseEntity = restTemplate.exchange("/headers", HttpMethod.GET, HttpEntity<Any>(headers), Map::class.java)

    // then
    val body = responseEntity.body
    assertThat(body!![IP]).isEqualTo("1.1.1.1")
  }
}

@RestController
class TestHeaderController {
  @GetMapping("/headers")
  fun headers(@RequestHeader headers: Map<Any, Any>, request: HttpServletRequest) = headers + (IP to request.remoteAddr)
}
