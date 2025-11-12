package com.ask.core.web

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody
import reactor.core.publisher.Flux
import java.time.Duration
import java.time.Instant

@RestController
@RequestMapping("/streaming")
class StreamingController {
  @GetMapping("/mvc")
  fun streaming(): ResponseEntity<StreamingResponseBody> {
    val streamingResponseBody = StreamingResponseBody { outputStream ->
      outputStream.bufferedWriter().use { writer ->
        repeat(5) {
          writer.write("${Instant.now()}\n")
          writer.flush()
          Thread.sleep(1000)
        }
      }
    }

    return ResponseEntity.ok()
      .contentType(MediaType.TEXT_EVENT_STREAM) // produces 에 설정하면 동작 안하고 직접 지정해야함
      .body(streamingResponseBody)
  }

  @GetMapping("/webflux", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
  fun webflux(): Flux<String> {
    return Flux.interval(Duration.ofSeconds(1))
      .take(5)
      .map { Instant.now().toString() }
  }
}
