# openfeign-querydsl

- Querydsl 유지 관리가 느려지면서 OpenFeign 에서 fork 하여 관리중
- https://github.com/OpenFeign/querydsl

## spring data jpa querydsl 관련 문구

- https://docs.spring.io/spring-data/jpa/reference/repositories/core-extensions.html

`Querydsl maintenance has slowed down to a point where the community has forked the project under OpenFeign at github.com/OpenFeign/querydsl (groupId io.github.openfeign.querydsl). Spring Data supports the fork on a best-effort basis.`

### 관련 GitHub Issue

- https://github.com/spring-projects/spring-data-jpa/issues/3335
- 2024 년 01월 29일, fork 에 대한 지원 계획 없다고함
- 2025 년 01월 22일, spring data jpa 에서 openfeign fork 를 지원하기로 결정

### CVE-2024-49203

- Querydsl vulnerable to HQL injection trough orderBy
- openfeign.querydsl 6.10.1 에서 해결됨
- https://github.com/advisories/GHSA-6q3q-6v5j-h6vg

## ksp 지원

- https://github.com/OpenFeign/querydsl/releases/tag/6.9
- https://github.com/OpenFeign/querydsl/blob/master/querydsl-examples/querydsl-example-ksp-codegen/build.gradle.kts

```kotlin
plugins {
  // kotlin 버전과 맞는 ksp version 사용
  kotlin("jvm") version "1.9.25"
  id("com.google.devtools.ksp") version "1.9.25-1.0.20"
}

val querydslVersion = 6.11

dependencies {
  implementation("io.github.openfeign.querydsl:querydsl-jpa:$querydslVersion")
  ksp("io.github.openfeign.querydsl:querydsl-ksp-codegen:$querydslVersion")
}
```

### kapt vs ksp

#### kapt

- Kotlin 코드에서 Java의 어노테이션 프로세서를 사용할 수 있도록 해주는 도구
- Kotlin 코드를 Java로 변환하고, 그 결과를 바탕으로 어노테이션 프로세서를 실행한다
- 추가적인 변환 단계가 있으므로 컴파일 시간이 늘어날 수 있다

#### ksp

- Kotlin용 으로 설계된 경량의 어노테이션 프로세싱 도구
- Kotlin 코드를 Java로 변환하지 않고 직접 처리하므로, Kapt에 비해 컴파일 속도가 빠르다

### KSP `@QueryProjection` 지원

- https://github.com/OpenFeign/querydsl/pull/997
- 중첩 `@QueryProjection` 대상 사용시 하단과 같이 생성자가 아닌 클래스에 추가해야함
- 생성자에 추가시 실패함

```kotlin
@QueryProjection
data class UserDto(
  val id: String,
  val dto: Name,
)

@QueryProjection
data class NestedUserDto(
  val id: String,
  val dto: UserDto,
)
```

## 현재 미지원 기능

- `@Entity` 에서 value class 사용
- 생성자를 통한 `@QueryProjection` 생성시 body 에 선언된 property 도 포함됨
  - querydsl ksp commiter 의 github 에 issue 생성 
  - https://github.com/IceBlizz6/querydsl-ksp/issues/9
  - https://github.com/OpenFeign/querydsl/pull/1232
    - 커미터가 반영해줌

## KSP 에 Value class 사용 PR 생성 시도

### 실패 원인 1

- ksp 사용시 코틀린 코드로 생성되므로 value class 로 QClass 를 생성되게 하면 value class 와 기저타입 맵핑간 이슈가 있음
- value class 를 where 절 조건에 사용할때 값 비교 실패로 예외 발생함
- spring data Jpa 에서 JpaRepository 사용시에 식별자 제네릭 타입 선언시 기저타입을 사용해야 하는 이슈와 유사함
  - https://github.com/spring-projects/spring-data-jpa/issues/2840

### 실패 원인 2

- value class 가 프로퍼티에 포함되어 있으면 `DefaultConstructorMarker` 가 생성자에 추가됨  
  따라서 리플렉션으로 생성자를 통해 `@QueryProjection` 대상 생성 시도할때 생성자를 찾지 못하여 예외 발생함
- Jackson 의 경우에도 유사한 이슈가 있음
  - https://github.com/FasterXML/jackson-module-kotlin/issues/651
