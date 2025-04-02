# redis

## Spring Data Redis

### Secondary Indexes

- https://docs.spring.io/spring-data/redis/reference/redis/redis-repositories/indexes.html
- index 를 생성해야 CrudRepository 에서 query method 대상으로 사용 가능하다.

```java
@RedisHash("people")
public class Person {
  @Id String id;
  @Indexed String firstname;
  String lastname;
  Address address;
}
```

## Keyspaces

- https://docs.spring.io/spring-data/redis/reference/redis/redis-repositories/keyspaces.html
- 키 스페이스 는 Redis Hash 의 실제 키를 생성하는 데 사용되는 접두사를 정의한다.

### default

- `getClass().getName()`
- `com.ask.redis.domain.WebsocketSession`
- 
### @RedisHash 를 통한 지정

- @RedisHash("websocket-session")

### 

```java
@Bean
public RedisMappingContext keyValueMappingContext() {
  return new RedisMappingContext(new MappingConfiguration(new IndexConfiguration(), new MyKeyspaceConfiguration()));
}

public static class MyKeyspaceConfiguration extends KeyspaceConfiguration {

  @Override
  protected Iterable<KeyspaceSettings> initialConfiguration() {
    return Collections.singleton(new KeyspaceSettings(Person.class, "people"));
  }
}
```

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

### @RedisHash ttl 설정시 삭제되지 않는 이슈

- 2번 항목에 대해서만 ttl 이 설정됨

```text
# ttl 만료 전
1) "websocket-session:session01:idx"
2) "websocket-session:session01"
3) "websocket-session:userId:user01"
4) "websocket-session"

# ttl 만료 후
1) "websocket-session:session01:idx"
2) "websocket-session:userId:user01"
3) "websocket-session"
```

원인은?

- index 와 Set 의 경우 notify-keyspace-events 를 통해 정리됨.
- RedisKeyValueAdapter.MappingExpirationListener 가 등록되어야 이벤트를 수신하여 처리됨.

```kotlin
// keyspaceNotificationsConfigParameter = "" 의 경우 redis 설정을 초기화 하는것이 아닌 바꾸지 않겠다는 설정
@EnableRedisRepositories(enableKeyspaceEvents = EnableKeyspaceEvents.ON_STARTUP, keyspaceNotificationsConfigParameter = "")
```
