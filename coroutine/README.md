# coroutine

## 코루틴 이란?

- Coroutine 이라는 용어는 co-(함께)와 routine(루틴)의 합성어
- 비동기 프로그래밍과 동시성(concurrency) 처리를 위한 프로그래밍 구조
- 함수나 루틴의 실행을 중단(suspend)하고 필요 시 다시 재개(resume)할 수 있는 특성을 가진다
- light-weight thread (경량 스레드) 라고도 한다
- 다양한 언어에서 코루틴을 지원 한다

### 스레드와 코루틴의 차이

| 특징         | 코루틴(Coroutine)                  | 스레드(Thread)                    |
|------------|---------------------------------|--------------------------------|
| 제어 방식      | 협력적 제어 (Cooperative Scheduling) | 선점형 제어 (Preemptive Scheduling) |
| 실행 단위      | 애플리케이션 레벨에서 실행 흐름 관리            | 운영체제 레벨에서 실행 흐름 관리             |
| 컨텍스트 전환 비용 | 낮음 (스택 스위칭 필요 없음)               | 높음 (스택과 레지스터 컨텍스트 전환 필요)       |
| 상태 관리      | 상태는 애플리케이션 코드에서 관리              | 상태는 운영체제가 관리                   |
| 수량         | 한 스레드에서 수천 개의 코루틴 실행 가능         | 스레드 수는 운영체제 및 하드웨어 자원에 제한됨     |
| 오버헤드       | 적음 (가벼움)                        | 높음 (스레드 스택, 스케줄링 비용)           |

### 코루틴 장점

1. 비선점 멀티태스킹
2. 동시성 프로그래밍 지원
3. 쉽고 가독성 있는 비동기 처리

### 코틀린 에서의 코루틴

- 코틀린 언어를 개발한 Jetbrain 에서는 멀티 쓰레딩 문제를 간소화된 비동기 작업 방식으로 해결할 수 있도록 코루틴을 개발함

## CoroutineContext 와 CoroutineScope

### CoroutineContext

-  코루틴이 실행될 환경 정보

#### Dispatchers.Default

- CPU 연산이 많은 작업에 적합
- 코어 수에 비례하는 스레드 풀 사용

#### Dispatchers.Main

- 안드로이드 앱의 메인 스레드(UI 스레드)에서 실행
- `kotlinx-coroutines-android` 의존성 필요

#### Dispatchers.IO

- 블로킹 IO 작업을 위한 코루틴 디스패처
- 공유된 스레드 풀을 사용
- 시스템 속성 `kotlinx.coroutines.io.parallelism` 로 제한 가능
    - 기본값: max(64, CPU 코어 수)

#### Dispatchers.Unconfined

- 현재 스레드에서 실행 되며 첫번째 중단 지점까지 실행
- 그이후에는 사용 가능한 스레드에서 실행
- 테스트나 디버깅에서 유용, 일반적인 사용 X

#### Custom Dispatcher

```kotlin
val dispatcher = Executors.newFixedThreadPool(4).asCoroutineDispatcher()

CoroutineScope(dispatcher).launch {
  // 실행
}
```

### CoroutineScope

- 코루틴을 실행할 수 있는 범위를 정의한다.
- 주로 launch, async 등을 사용해서 코루틴을 시작할 때 사용
- 내부 코루틴이 비동기로 실행됨

## Coroutine Builder

- Coroutine을 실행할 때 사용하는 여러 가지 함수

### runBlocking

- 코루틴이 끝날 때까지 현재 스레드를 블로킹한다
- 현재 스레드를 차단하고, 내부 코루틴이 완료될 때까지 기다림

> It is designed to bridge regular blocking code to libraries that are written in suspending style,  
to be used in main functions and in tests.

### launch

- 현재 스레드를 블로킹하지 않고 새로운 코루틴을 실행할 수 있으며 특정 결과값 없이 Job 객체를 반환
- `join()` 을 사용하여 대기 가능

### async

- launch와 다르게 async는 결과를 반환하며 결과값은 `Deferred` 로 감싸서 반환
- `await()` 를 사용하여 대기 가능

### withContext

- async 와 다르게 Deferred<T>객체로 반환하지 않고, 결과(T)를 반환

## Java Virtual Thread vs. Kotlin Coroutine

### Java Virtual Thread

- thread per request(spring mvc) 에 적합하고 기존 코드에서 손쉽게 변경 가능

### Kotlin Coroutine

- 높은 동시성을 요구하거나 Event 기반 시스템, 계층구조가 있는작업(Structured Concurrency)을 처리하거나 취소할때 적합

## Structured Concurrency

## Spring MVC 에서의 코루틴 사용

- thread per request 구조에선 요청을 처리하는 스레드가 블로킹 되므로 응답을 위함이라면 runBlocking + async 를 사용하고  
  단순 비동기라면 CoroutineScope + launch 를 사용하면 된다.
- 병렬 처리를 위함이라면 코루틴을 사용하는것보다 VT 를 사용하는 확장 함수를 만드는것이 훨씬 간단하다.


## 참조

- https://dev.gmarket.com/82
