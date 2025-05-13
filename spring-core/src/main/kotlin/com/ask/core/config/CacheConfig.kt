package com.ask.core.config

import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration

@Configuration
@EnableCaching
class CacheConfig {
  @Bean
  fun cacheCustomizer() = CacheManagerCustomizer<CaffeineCacheManager> { cacheManager ->
    Cache.TTL_MAP.forEach {
      cacheManager.registerCustomCache(it.key, Caffeine.newBuilder().expireAfterWrite(it.value).build())
    }
  }
}

object Cache {
  const val USER = "user"

  val TTL_MAP = mapOf(
    USER to Duration.ofHours(1),
  )
}
