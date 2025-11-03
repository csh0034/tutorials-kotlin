# warmup

## warmup 이란?

- 서버나 애플리케이션이 시작된 직후 일정 시간 동안, 혹은 요청 처리 전 미리 필요한 리소스나 컴포넌트를 준비해두는 과정
- 즉, 애플리케이션이 `준비 완료` 상태로 안정적으로 요청을 처리하기 전에, 미리 예열해두는 단계

## jvm warmup 이 필요한 이유

### Cold Start

- 서버 실행 직후, 배포 직후 속도가 느려지는 현상, 초기 응답 지연 발생
- 서버나 애플리케이션이 막 기동된 직후, 아직 필요한 리소스들이 초기화되지 않아 첫 요청의 응답이 느려지는 현상

## 느려지는 원인

### 클래스 로더

- 일반적으로 Lazy Loading 방식으로 동작  
- 배포 직후에는 대부분의 클래스들이 한번도 사용되지 않았으므로 클래스 로더에 의해 메모리에 적재되지 않은 상태

### JIT 컴파일러

- JIT 컴파일러는 실행 중에 바이트코드를 기계어로 변환하여 성능을 향상시킨다
- 모든 코드를 다 컴파일하지 않고, 자주 실행되는 핫스팟 코드만 최적화한다
- 핫스팟을 찾기 위해 **프로파일링**을 수행하고, 변환된 코드는 코드 캐시에 저장된다
  -  애플리케이션의 동작을 분석하고 코드 실행 횟수, 루프 반복 횟수, 메소드 호출 등의 정보를 측정하고 기록
- 오라클 JVM에서 JIT 컴파일러를 Hotspot이라고 부른다

## 해결 방법

### 1. warmup

- 주로 사용하며 자주 사용하는 대상에 대하여 어플리케이션 실행 시점에 호출하는 방식

### 2. cds 적용

- cds 를 사용하면 JVM이 클래스 로딩을 처음부터 하나씩 하지 않고, 공유된 클래스 데이터를  
  바로 메모리에 올림 첫 요청 시 클래스 로딩으로 인한 지연이 줄어든다.
- 따라서 Cold Start 지연 시간을 어느 정도 감소

### 3. native image 적용 (aot)

- Native Image를 사용하면 클래스 로딩과 JIT 컴파일 같은 JVM 초기화 비용이 사라져 warmup 이  
  거의 필요 없지만 DB 연결이나 캐시 적재처럼 애플리케이션 수준의 예열은 필요함

## 참조

- https://oliveyoung.tech/2024-10-30/application_warmup_algorithm/
- https://engineering.linecorp.com/ko/blog/apply-warm-up-in-spring-boot-and-kubernetes
- https://hudi.blog/jvm-warm-up/
- https://goddaehee.tistory.com/356
- https://tech.kakaopay.com/post/2024-google-cloud-next-2/
