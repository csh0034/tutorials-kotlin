package com.ask.virtualthread

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

@Disabled
class VirtualThreadTest {
  @Test
  fun `platform thread 1만개 생성`() {
    (1..10_000).map { Thread {} }
      .forEach { it.start() }
  }

  @Test
  fun `virtual thread 100만개 생성`() {
    (1..1_000_000).map { Thread.ofVirtual().unstarted { } }
      .forEach { it.start() }
  }
}
