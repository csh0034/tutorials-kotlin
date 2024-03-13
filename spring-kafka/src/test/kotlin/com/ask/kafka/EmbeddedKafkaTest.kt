package com.ask.kafka

import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@EmbeddedKafka(partitions = 1)
@ActiveProfiles("test")
class EmbeddedKafkaTest {

  private val log = LoggerFactory.getLogger(this::class.java)

  @Test
  fun test() {
    log.debug("test")
  }

}
