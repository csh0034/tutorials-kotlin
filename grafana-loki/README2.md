# grafana-loki

## docker-compose 를 통한 설치

- https://grafana.com/docs/loki/latest/setup/install/docker/#install-with-docker-compose

```shell
$ mkdir loki
$ cd loki
$ wget https://raw.githubusercontent.com/grafana/loki/v3.0.0/production/docker-compose.yaml -O docker-compose.yml
$ docker-compose up -d
```

## Spring Boot 로그를 loki 로 전송하는 방법

### 1. Promtail

#### Promtail Pipeline

파이프라인 단계는 로그 항목과 해당 label 을 변환하는 데 사용된다.  
대부분의 경우 regex 또는 json 단계를 사용하여 로그에서 데이터를 추출한다.   
추출된 데이터는 임시 map 객체로 변환되며 이는 Promtail 에서 사용할 수 있다.   
label 또는 output. 추가적으로 docker 및 cri 를 제외한 다른 모든 단계에서 추출된 데이터에 액세스할 수 있다.

하단의 단계(stages)를 거친다.

1. 파싱 단계(Parsing stages), docker/cri/regex/json/multiline...
2. 변환 단계(Transform stages), template/pack...
3. 액션 단계(Action stages), labels/timestamp/output...
4. 필터링 단계(Filtering stages), match/drop

- https://grafana.com/docs/loki/latest/send-data/promtail/configuration/#pipeline_stages
- https://grafana.com/docs/loki/latest/send-data/promtail/pipelines/
- https://grafana.com/docs/loki/latest/send-data/promtail/stages/

#### docker-compose.yml 

- `-config.expand-env=true` 를 통해 env 사용하도록 설정
  - https://grafana.com/docs/loki/latest/send-data/promtail/configuration/#use-environment-variables-in-the-configuration

```yaml
services:
  promtail:
    image: grafana/promtail:2.9.2
    volumes:
      - ./config.yml:/etc/promtail/config.yml
    command: -config.file=/etc/promtail/config.yml -config.expand-env=true
```

#### promtail config

- multiline 설정
  - stacktrace 등과 같이 개행 되더라도 하나의 로그로 처리 
  - 주의할점) expression 맨뒤에 `(?s:.*)` 추가하여 개행되더라도 capture 되도록 해야함
  - https://grafana.com/docs/loki/latest/send-data/promtail/stages/multiline/#multiline
- regex 설정
  - 로그 포맷을 지정하며 label 에 쓸수 있도록 capture 해야함 
  - https://grafana.com/docs/loki/latest/send-data/promtail/stages/regex/

```yaml
server:
  http_listen_port: 9080
  grpc_listen_port: 0

positions:
  filename: /tmp/positions.yaml

clients:
  - url: http://host.docker.internal:3100/loki/api/v1/push

scrape_configs:
  - job_name: app
    static_configs:
      - targets:
          - localhost
        labels:
          job: logdemo
          __path__: /app.log
          hostname: ${HOSTNAME}
    pipeline_stages:
      - multiline:
          firstline: '^[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}'
      - regex:
          expression: '^(?P<timestamp>[^ ]+)\s+(?P<level>[A-Z]+)\s+\d+\s+---\s+\[(?P<name>[^\]]+)\](?s:.*)$'
      - labels:
          name:
          level:
```

### 2. loki-logback-appender

- https://github.com/loki4j/loki-logback-appender
- https://loki4j.github.io/loki-logback-appender/#quick-start
- [Logging in Spring Boot With Loki](https://www.baeldung.com/spring-boot-loki-grafana-logging)

#### spring boot 기본 로그포맷 사용하면서 label 추가 

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
  <include resource="org/springframework/boot/logging/logback/defaults.xml"/>
  <include resource="org/springframework/boot/logging/logback/console-appender.xml"/>

  <springProperty name="name" source="spring.application.name"/>

  <appender name="LOKI" class="com.github.loki4j.logback.Loki4jAppender">
    <http>
      <url>http://host.docker.in1212ternal:3100/loki/api/v1/push</url>
      <requestTimeoutMs>5000</requestTimeoutMs>
    </http>
    <format>
      <label>
        <pattern>app=${name},host=${HOSTNAME},level=%level</pattern>
        <readMarkers>true</readMarkers>
      </label>
      <message>
        <pattern>${CONSOLE_LOG_PATTERN}</pattern>
      </message>
    </format>
  </appender>

  <root level="INFO">
    <appender-ref ref="LOKI"/>
    <appender-ref ref="CONSOLE"/>
  </root>
</configuration>
```

### 3. Docker driver client

- https://grafana.com/docs/loki/latest/send-data/docker-driver/#docker-driver-client

## otel 도입 전/후

### otel 도입 전

![img.png](img/09.png)

### otel 도입 후

![img.png](img/08.png)

## blog post

- https://tech.scatterlab.co.kr/spring-boot-monitoring-with-prometheus/
- [Kubernetes 도입 후의 Observability 개선기](https://medium.com/@minina1868/kubernetes%EB%A5%BC-%EC%9D%B4%EC%9A%A9%ED%95%9C-observability-%EA%B0%9C%EC%84%A0%EA%B8%B0-with-spring-tempo-promtail-prometheus-loki-grafana-65f9a609c0bc)
- [OpenTelemetry, Grafana, Loki, Tempo, Prometheus를 활용한 Spring Boot Observability 구성하기](https://medium.com/@dudwls96/opentelemetry-grafana-loki-tempo-prometheus%EB%A5%BC-%ED%99%9C%EC%9A%A9%ED%95%9C-spring-boot-observability-%EA%B5%AC%EC%84%B1%ED%95%98%EA%B8%B0-f977df45bb70)
- [K8S/EKS에서 Opentelemetry Operator 사용 방법](https://medium.com/@junong29/k8s-eks%EC%97%90%EC%84%9C-opentelemetry-operator-%EC%82%AC%EC%9A%A9-%EB%B0%A9%EB%B2%95-5786891147d0)
- [OpenTelemetry 설치 준비: Operator 설치](https://www.anyflow.net/sw-engineer/install-opentelemetry-operator)
- [Kubernetes 환경에서 OpenTelemetry Collector 구성하기](https://medium.com/@dudwls96/kubernetes-%ED%99%98%EA%B2%BD%EC%97%90%EC%84%9C-opentelemetry-collector-%EA%B5%AC%EC%84%B1%ED%95%98%EA%B8%B0-d20e474a8b18)
