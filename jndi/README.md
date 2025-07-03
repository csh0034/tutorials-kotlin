# jndi

## jndi(Java Naming and Directory Interface) 란?

- Java 애플리케이션이 외부 리소스에 접근할 수 있도록 해주는 디렉터리 서비스 인터페이스
- jdbc/mydb 등과같이 선언하면 접두사를 추가하여 java:comp/env/jdbc/mydb 로 접근
    - java:comp/env/jdbc
    - java:comp/env/main
    - ...
- tomcat context.xml or server.xml 등에 Resource 로 선언하고 코드에서 이름을 통해 접근한다
- was 에 정보를 두기 때문에 각 어플리케이션은 정보를 관리하지 않아도 된다.

## Example

### 내장톰캣을 사용하는 경우

- 어차피 접근하는 어플리케이션은 하나 이므로 해당 기능 필요없음

### 내장톰캣을 사용하지 않는 경우

- 외장톰캣의 경우 여러 어플리케이션이 실행될수 있으므로 공용으로 관리하기 위해 사용할 수 있음
- `spring.datasource.jndi-name` 프로퍼티를 통해 db 연결 가능
- jdbc 사용시 dbcp2 를 사용한다면 tomcat lib($CATALINA_HOME/lib) 에 직접 추가해야함
    - commons-dbcp2 + commons-pool2, DBCP
    - mysql-connector-j, JDBC Driver
- 모두 동일한 리소스 사용해도 된다면 context.xml 에 Resource 바로 추가해도 됨

```xml
<!-- server.xml -->
<GlobalNamingResources>
  <Resource name="jdbc/mydb"
    auth="Container"
    type="javax.sql.DataSource"
    factory="org.apache.commons.dbcp2.BasicDataSourceFactory"
    driverClassName="org.mariadb.jdbc.Driver"
    url="jdbc:mariadb://localhost:3306/mydb"
    username="root"
    password="111111"
    maxTotal="20"
    maxIdle="20"
    maxWaitMillis="10000"/>
</GlobalNamingResources>
```

```xml
<!-- context.xml -->
<Context>
  <ResourceLink name="jdbc/mydb" global="jdbc/mydb" type="javax.sql.DataSource"/>
</Context>
```

## 톰캣 문서

- https://tomcat.apache.org/tomcat-9.0-doc/jndi-resources-howto.html#Global_configuration

> Tomcat specific resource configuration is entered in the <Context> elements that can be specified in either $CATALINA_BASE/conf/server.xml or, preferably, the per-web-application context XML file (META-INF/context.xml).

- META-INF 하위에 context.xml 을 두고 개별 설정하는걸 권장


## Troubleshooting

- factory class 인 BasicDataSourceFactory 에서 `org.apache.commons.logging` 이 필요함
- spring 에선 apache commons logging 보다 spring-jcl 을 통해 처리하는것을 권장함
- 따라서 tomcat/lib 에 spring-jcl 도 추가해야함

```text
Caused by: java.lang.ExceptionInInitializerError: Exception java.lang.NoClassDefFoundError: org/apache/commons/logging/LogFactory [in thread "main"]
	at org.apache.commons.dbcp2.BasicDataSourceFactory.<clinit>(BasicDataSourceFactory.java:64)
```
