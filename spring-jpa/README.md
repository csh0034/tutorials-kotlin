# spring-jpa

## Hibernate Batching

- https://docs.jboss.org/hibernate/orm/7.1/userguide/html_single/Hibernate_User_Guide.html#batch
- 옵션
  - hibernate.jdbc.batch_size
    - Hibernate 가 한 번에 묶어서 실행할 SQL 문장의 최대 개수를 지정, 10 ~ 50 권장
  - hibernate.order_inserts
    - Hibernate 가 INSERT 쿼리를 실행할 때, 엔티티 타입별로 정렬해서 실행
    - 단, 정렬 과정이 추가되므로 성능 오버헤드가 발생할 수 있으므로 전후 성능 비교후 적용 권장
  - hibernate.order_updates
    - Hibernate 가 UPDATE 쿼리를 실행할 때, 엔티티 타입 + 기본키 순서로 정렬해서 실행
    - 같은 엔티티에 대한 UPDATE가 묶이면서 더 많은 배치 실행
    - 동시에 데드락(deadlock) 발생 가능성이 줄어든다
    - 단, 정렬 과정이 추가되므로 성능 오버헤드가 발생할 수 있으므로 전후 성능 비교후 적용 권장

## Query Settings

- https://docs.jboss.org/hibernate/orm/7.1/userguide/html_single/Hibernate_User_Guide.html#settings-query
- 옵션
  - hibernate.in_clause_parameter_padding
      - Where 조건에서 In절의 Padding Cache 사용 유무 (default: false)

## Fetch Related Settings

- https://docs.jboss.org/hibernate/orm/7.1/userguide/html_single/Hibernate_User_Guide.html#settings-fetch
- 옵션
  - hibernate.default_batch_fetch_size
    - Hibernate가 batch fetching의 기본 크기(default value) 를 지정하는 설정  
      기본적으로는 @BatchSize 어노테이션이 붙은 엔티티나 컬렉션에만 batch fetching이 적용된다  
      하지만 hibernate.default_batch_fetch_size를 지정하면  
      명시적 어노테이션이 없어도 기본적으로 batch fetching을 수행한다  
    - OneToMany Lazy 로딩시에 in 절을 통해 한번에 가져온다

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

## `@ColumnTransformer` 를 통한 컬럼 암복호화

```kotlin
@Column(nullable = false)
@ColumnTransformer(
  read = "cast(aes_decrypt(unhex(email), 'secret') as char)",
  write = "hex(aes_encrypt(?, 'secret'))"
)
val email: String
```

- 상단 방식의 단점: annotation 에 세팅되어야 하므로 secret key 가 하드코딩 되어야함

### StatementInspector 사용

- JDBC 구문 이 준비되기 전에 세션에서 실행한 각 SQL 명령을 검사하고 처리 가능
- 해당 인터페이스를 통하여 sql 실행전에 치환

```kotlin
const val SECRET_KEY_PLACEHOLDER = "__SECRET_KEY__"

@Component
class EncryptionStatementInspector : StatementInspector {
  private val log = LoggerFactory.getLogger(javaClass)

  @Value("\${custom.db-secret-key}")
  private lateinit var secretKey: String

  override fun inspect(sql: String): String {
    if (sql.contains(SECRET_KEY_PLACEHOLDER)) {
      return sql.replace(SECRET_KEY_PLACEHOLDER, "'${secretKey}'").also {
        log.debug("replaced sql: $it")
      }
    }

    return sql
  }
}
```

### Integrator 사용

- Contract for extensions that integrate with Hibernate
- entity metadata 에 접근하여 @ColumnTransformer 를 통해 설정된 customRead, customWrite 를 변경할 수 있음

```kotlin
@Component
class EncryptionIntegrator : Integrator {
  @Value("\${custom.db-secret-key}")
  private lateinit var secretKey: String

  override fun integrate(
    metadata: Metadata,
    bootstrapContext: BootstrapContext,
    sessionFactory: SessionFactoryImplementor,
  ) {
    metadata.entityBindings.forEach { entity ->
      entity.referenceableProperties.forEach { property ->
        property.columns.forEach { column ->
          val customRead = column.customRead
          if (customRead != null && customRead.contains(SECRET_KEY_PLACEHOLDER)) {
            column.customRead = customRead.replace(SECRET_KEY_PLACEHOLDER, "'${secretKey}'")
          }

          val customWrite = column.customWrite
          if (customWrite != null && customWrite.contains(SECRET_KEY_PLACEHOLDER)) {
            column.customWrite = customWrite.replace(SECRET_KEY_PLACEHOLDER, "'${secretKey}'")
          }
        }
      }
    }
  }

  override fun disintegrate(
    sessionFactory: SessionFactoryImplementor,
    serviceRegistry: SessionFactoryServiceRegistry
  ) {
    // no-op
  }
}
```

### hibernate custom function 사용 가능?

- `@ColumnTransformer` 의 경우 entity 에서 read write 를 통해 바로 db 에 접근하므로 hibernate function 동작 안함

## Troubleshooting

## value class Entity Id 사용시 이슈

1. entity Id 에 vale class 사용시 컴파일 시점에 원래 타입이 사용됨.
2. `ProjectJpaRepository extends JpaRepository<ProjectJpa, ProjectId>` 등과 같이   
    value class generic 으로 선언시 식별자 불일치가 발생
3. `ProjectJpaRepository extends JpaRepository<ProjectJpa, String>` 으로 해야함.

- https://github.com/spring-projects/spring-data-jpa/issues/2840
