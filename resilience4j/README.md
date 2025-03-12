# resilience4j

## resilience4j 란 ?

- Resilience4j 는 함수형 프로그래밍을 위해 설계된 경량 **fault tolerance** 라이브러리이다.
- Netflix 의 Hystrix 가 유지보수로 전환된 이후 대체제로 많이 사용된다.

## Core Modules

### 1. CircuitBreaker

오류가 지속되면 회로를 열어 이후의 요청을 차단  
과부하를 방지하고 빠르게 장애를 감지

다음 3가지 상태를 가진다

- Closed
  - 정상 상태
- Open
  - 장애가 감지되어 모든 요청을 차단
- Half-Open
  - 일부 요청만 허용하여 상태 확인

**슬라이딩 윈도우** 를 사용하여 호출 결과를 저장하고 집계한다

> 슬라이딩 윈도우: 데이터 스트림에서 최근 일정 기간(또는 개수)의 데이터를 유지하며 계산하는 방식

- Count-based sliding window(default)
  - 마지막 N개 호출의 결과를 집계
- Time-based sliding window
  - 마지막 N초 호출의 결과를 집계

### 2. Bulkhead

동시 실행 횟수를 제한 하여 특정 서비스가 과부하 되지 않도록 방지

- SemaphoreBulkhead
  - 세마포어를 사용
- FixedThreadPoolBulkhead
  - 유한 큐와 고정 스레드 풀을 사용

### 3. RateLimiter

단위 시간동안 얼마만큼의 실행을 허용할 것인지 제한  
중요한 요청의 경우 실패시 Retry, BackOff 등의 전략을 함께 사용하는것이 권장

### 4. Retry

네트워크 장애 또는 일시적인 오류 발생 시 설정한 횟수 만큼 자동 재시도

### 5. TimeLimiter

지정된 시간 내에 완료되지 않으면 실패 처리하는 방식으로, 과도한 대기 시간을 방지

## Code

### Dependency

- Spring Cloud 를 통해 사용 하는 방법과 직접 사용하는 방법이 있다.

```kotlin
// 1. Spring Cloud, Circuit breaker 구현에 대한 추상화 제공
dependencyManagement {
  imports {
    mavenBom("org.springframework.cloud:spring-cloud-dependencies:2024.0.0")
  }
}

implementation("org.springframework.cloud:spring-cloud-starter-circuitbreaker-resilience4j")

// 2. resilience4j 직접 사용
implementation("io.github.resilience4j:resilience4j-spring-boot3")
```

## 참조

- https://resilience4j.readme.io/docs/getting-started
- https://docs.spring.io/spring-cloud-circuitbreaker/reference/index.html
