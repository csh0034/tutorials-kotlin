# spring-jpa

## MariaDB Batch Insert

### jdbc 수준에서 batch insert 활성화

rewriteBatchedStatements 의 경우 MariaDB Connector/J **3.0.0** 에서 제거됨  
spring boot 3.3 기준 mariadb client **3.3.3** 사용함

*MariaDB 는 별다른 설정 안해도 된다.  

*식별자 생성에 IDENTITY 방식을 사용하면 Hibernate 가 JDBC 수준에서 batch insert 를 비활성화함

- https://docs.jboss.org/hibernate/orm/6.5/userguide/html_single/Hibernate_User_Guide.html#batch-session-batch-insert

추가, MariaDB Connector/J **3.5.6** 에서 옵션 다시 추가됨

- https://mariadb.com/docs/release-notes/connectors/java/3.5/3.5.6
- https://jira.mariadb.org/browse/CONJ-1238
- https://jira.mariadb.org/browse/CONJ-1077

### hibernate batch 설정 활성화

hibernate.jdbc.batch_size

- 드라이버에게 배치 실행을 요청하기 전에 Hibernate 가 함께 배치할 명령문의 최대 수를 제어합니다. 0 또는 음수는 이 기능을 비활성화.
- [https://docs.jboss.org/hibernate/orm/6.5/userguide/html_single/Hibernate_User_Guide.html#batch-jdbcbatch]

### Logging

- https://mariadb.com/kb/en/about-mariadb-connector-j/#easy-to-use-logging

```xml

<configuration>
  <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
    <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
      <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n</pattern>
    </encoder>
  </appender>

  <logger name="org.mariadb.jdbc" level="trace" additivity="false">
    <appender-ref ref="STDOUT"/>
  </logger>

  <root level="error">
    <appender-ref ref="STDOUT"/>
  </root>
</configuration>
```

### docs

- https://mariadb.com/kb/en/about-mariadb-connector-j/
- https://mariadb.com/docs/server/connect/programming-languages/java/upgrade
- https://mariadb.com/kb/en/how-to-quickly-insert-data-into-mariadb/#inserting-data-with-insert-statements

## Troubleshooting

## value class Entity Id 사용시 이슈

1. entity Id 에 vale class 사용시 컴파일 시점에 원래 타입이 사용됨.
2. `ProjectJpaRepository extends JpaRepository<ProjectJpa, ProjectId>` 등과 같이   
    value class generic 으로 선언시 식별자 불일치가 발생
3. `ProjectJpaRepository extends JpaRepository<ProjectJpa, String>` 으로 해야함.

- https://github.com/spring-projects/spring-data-jpa/issues/2840
