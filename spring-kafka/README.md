# spring-kafka

## Confluent Platform

### Confluent Platform Overview

- https://docs.confluent.io/platform/current/platform.html

### Supported Versions and Interoperability for Confluent Platform

- https://docs.confluent.io/platform/current/installation/versions-interoperability.html#

| Confluent Platform | Apache Kafka® | Release Date     |
|--------------------|---------------|------------------|
| 7.6.x              | 3.6.x         | February 9, 2024 |
| 7.5.x              | 3.5.x         | August 25, 2023  |
| 7.3.x              | 3.4.x         | May 3, 2023      |
| ...                | ...           | ..               |

### Docker Image Configuration Reference for Confluent Platform

- https://docs.confluent.io/platform/current/installation/docker/config-reference.html

#### confluent-local

## Amazon MSK(Managed Streaming for Apache Kafka)

- https://aws.amazon.com/ko/msk/

### 지원 되는 Apache Kafka 버전

- https://docs.aws.amazon.com/ko_kr/msk/latest/developerguide/supported-kafka-versions.html

## kafka vs rabbitmq

- https://aws.amazon.com/ko/compare/the-difference-between-rabbitmq-and-kafka/
- https://tech.kakao.com/2021/12/23/kafka-rabbitmq/

## DLT Strategies

- https://docs.spring.io/spring-kafka/reference/retrytopic/dlt-strategies.html

### integration flow 사용시 dlt 지정

- DefaultErrorHandler 선언시 DeadLetterPublishingRecoverer 사용하여 처리

### KafkaListener 사용시 dlt 지정

- `@RetryableTopic`, `@KafkaListener`, `@DltHandler`
- 상단 방식 사용시 하단 클래스에서 DefaultErrorHandler 에 DeadLetterPublishingRecoverer 를 세팅하여 처리됨
  - RetryTopicConfigurer
    - ListenerContainerFactoryConfigurer.decorate
      - CommonErrorHandler

### Retry Topic

- 3.2 부터는 기본 동작이 동일한 지연 간격에 대해서는 재시도 토픽을 재사용하도록 변경
  - spring boot 3.5.6 기준 spring kafka 3.3.10
  - `SameIntervalTopicReuseStrategy.SINGLE_TOPIC`
- 지수 백오프(exponential backoff)를 사용할 경우, 재시도 토픽은 지연 시간 값이 접미사로 추가된다
