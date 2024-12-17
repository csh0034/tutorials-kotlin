# Traditional Deployment

## 1. build.gradle.kts 수정

### war plugin 추가

```kotlin
plugins {
  kotlin("jvm") version "1.9.25"
  kotlin("plugin.spring") version "1.9.25"
  id("org.springframework.boot") version "3.4.0"
  id("io.spring.dependency-management") version "1.1.6"
  war // 추가
}
```

## 2. SpringBootServletInitializer 추가

```kotlin
class ServletInitializer : SpringBootServletInitializer() {
  override fun configure(application: SpringApplicationBuilder): SpringApplicationBuilder {
    return application.sources(TraditionalDeploymentApplication::class.java)
  }
}
```

## 3. 의존성 정의 변경

providedRuntime: runtime 시에만 필요하며 실행환경에서 제공되는 dependency

- war plugin 에서 지원됨
- compileOnly 보다 권장됨

```kotlin
dependencies {
  // ... 
  providedRuntime("org.springframework.boot:spring-boot-starter-tomcat")
  // ...
}
```

## ref

- https://docs.spring.io/spring-boot/how-to/deployment/traditional-deployment.html
