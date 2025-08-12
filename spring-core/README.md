# spring-core

## logging

logback-spring.xml 과 application.yml 에 동일한 패키지에 로그 설정하는 경우  
application.yml 의 logging.level 우선순위가 높다.

spring logback 처리 클래스 DefaultLogbackConfiguration

### logback-spring.xml

- console, file 등에 대해서 미리 선언 되어있으므로 include 만 하면 된다.
  - spring-boot:version/org.springframework.boot.logging.logback
    - base.xml (console, file 모두 사용시)
    - defaults.xml (기본값 선언)
    - console-appender.xml
    - file-appender.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration scan="true" scanPeriod="5 seconds">
  <include resource="org/springframework/boot/logging/logback/base.xml"/>

  <property name="LOG_FILE" value="D:\WEB\docimage\logs\docimage.log"/>
  <property name="LOGBACK_ROLLINGPOLICY_FILE_NAME_PATTERN" value="${LOG_FILE}.%d{yyyy-MM-dd}.%i"/>

  <logger name="com.rsupport" level="debug"/>
</configuration>
```

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

## h2 in-memory 사용 관련

기본적으로 데이터베이스에 대한 마지막 연결을 닫으면 데이터베이스도 닫힌다.  
인메모리 데이터베이스의 경우 이는 콘텐츠가 손실됨을 의미한다.   
가상 머신이 살아 있는 동안 메모리 내 데이터베이스의 내용을 유지하려면 데이터베이스 URL에 `;DB_CLOSE_DELAY=-1` 을 추가하면됨.

`;DB_CLOSE_ON_EXIT=FALSE` 의 경우 vm 종료시 DB 닫기 안하는 옵션이므로 in-memory 사용시엔 필요없다. 

- https://www.h2database.com/html/features.html#in_memory_databases
- https://stackoverflow.com/questions/52480973/h2-in-memory-test-db-closed-despite-db-close-on-exit-false
