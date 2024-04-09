# spring-core

## logging

logback-spring.xml 과 application.yml 에 동일한 패키지에 로그 설정하는 경우  
application.yml 의 logging.level 우선순위가 높다.

spring logback 처리 클래스 DefaultLogbackConfiguration

## Parameter

### Instant 처리

#### query param

- InstantFormatter 
- default ms 로 처리

#### request body

- jackson, InstantDeserializer
- default ns 로 처리
- 하단 설정 추가시 ms 로 처리

```yaml
spring:
  jackson:
    deserialization:
      read-date-timestamps-as-nanoseconds: false

```
