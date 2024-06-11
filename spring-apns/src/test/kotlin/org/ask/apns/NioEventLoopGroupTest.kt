package org.ask.apns

import io.netty.channel.nio.NioEventLoopGroup
import org.junit.jupiter.api.Test

class NioEventLoopGroupTest {
  @Test
  fun execute() {
    val nioEventLoopGroup = NioEventLoopGroup(1)
    for (i in 1..10) {
      nioEventLoopGroup.execute { println("[${Thread.currentThread().name}] $i") }
    }
  }
}
