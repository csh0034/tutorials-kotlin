package com.ask.redis.config

import com.ask.redis.domain.WebsocketSession
import com.fasterxml.jackson.databind.ObjectMapper
import org.redisson.spring.starter.RedissonAutoConfigurationCustomizer
import org.springframework.boot.autoconfigure.cache.CacheProperties
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer
import org.springframework.boot.autoconfigure.condition.ConditionalOnThreading
import org.springframework.boot.autoconfigure.thread.Threading
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisKeyValueAdapter.EnableKeyspaceEvents
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.convert.KeyspaceConfiguration
import org.springframework.data.redis.core.convert.MappingConfiguration
import org.springframework.data.redis.core.index.IndexConfiguration
import org.springframework.data.redis.core.mapping.RedisMappingContext
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.RedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
import java.time.Duration
import java.util.concurrent.Executors


@Configuration
@EnableCaching
@EnableRedisRepositories(enableKeyspaceEvents = EnableKeyspaceEvents.ON_STARTUP, keyspaceNotificationsConfigParameter = "")
class RedisConfig(
  private val redisConnectionFactory: RedisConnectionFactory,
  private val cacheProperties: CacheProperties,
  private val objectMapper: ObjectMapper,
) {
  @Bean
  fun redisTemplate() = RedisTemplate<String, Any>().apply {
    connectionFactory = redisConnectionFactory
    keySerializer = StringRedisSerializer()
    valueSerializer = GenericJackson2JsonRedisSerializer(objectMapper)
  }

  @Bean
  fun cacheManagerBuilderCustomizer(): RedisCacheManagerBuilderCustomizer {
    return RedisCacheManagerBuilderCustomizer { it.withInitialCacheConfigurations(cacheExpiresMap()) }
  }

  private fun cacheExpiresMap(): Map<String, RedisCacheConfiguration> {
    return Caches.TTL_MAP.keys
      .associateWith { redisExpiresConfiguration(Caches.TTL_MAP[it]!!) }
  }

  private fun redisExpiresConfiguration(duration: Duration): RedisCacheConfiguration {
    return RedisCacheConfiguration.defaultCacheConfig(Thread.currentThread().contextClassLoader)
      .prefixCacheNameWith(cacheProperties.redis.keyPrefix)
      .entryTtl(duration)
      .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.json()))
  }

  @Bean
  fun keyValueMappingContext(): RedisMappingContext {
    return RedisMappingContext(MappingConfiguration(IndexConfiguration(), MyKeyspaceConfiguration()))
  }

  class MyKeyspaceConfiguration : KeyspaceConfiguration() {
    override fun initialConfiguration(): Iterable<KeyspaceSettings> {
      return setOf(KeyspaceSettings(WebsocketSession::class.java, "ws"))
    }
  }
}

@Configuration
class RedissonConfig {
  @Bean
  @ConditionalOnThreading(Threading.VIRTUAL)
  fun redissonAutoConfigurationCustomizer() = RedissonAutoConfigurationCustomizer {
    it.setNettyExecutor(Executors.newVirtualThreadPerTaskExecutor())
  }
}

object Caches {
  const val JWK = "jwk"

  val TTL_MAP = mapOf(
    JWK to Duration.ofHours(1),
  )
}
