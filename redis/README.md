# redis

## Redisson

- https://github.com/redisson/redisson

### 분산락 AOP

- [분산락 사용, Kurly Tech Blog](https://helloworld.kurly.com/blog/distributed-redisson-lock/)

## Troubleshooting

### lettuce 가 아닌 redisson client 사용시 @DataRedisTest 실패

- repository bean 을 찾지 못하여 실패함
- Redisson 설정을 import 해야함

```text
No qualifying bean of type 'com.ask.redis.domain.WebsocketSessionRepository'
```

```kotlin
@DataRedisTest
@Import(RedissonAutoConfigurationV2::class)
```
