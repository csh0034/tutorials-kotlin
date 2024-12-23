# cloud-aws

## S3

- https://docs.awspring.io/spring-cloud-aws/docs/3.2.1/reference/html/index.html#spring-cloud-aws-s3

### S3ProtocolResolver

- `s3://` 경로로 시작하는 resource 를 찾도록 지원

### ProtocolResolver

- resource 를 프로토콜에 따라 처리할수 있도록 지원
- ResourceLoader 에 등록

## oci Buckets, s3 sdk upload

- https://docs.oracle.com/en-us/iaas/Content/Object/Tasks/s3compatibleapi.htm

### 설정 1

- Amazon S3 Compatibility AP I는 경로 스타일 URL만 지원한다. V2(가상 호스팅) 스타일 URL은 지원되지 않는다.

```yaml
spring:
  cloud:
    aws:
      s3:
        path-style-access-enabled: true
```

### 설정 2

- Amazon S3 Compatibility api 에 Compartment 설정
- https://docs.oracle.com/en-us/iaas/Content/Object/Tasks/designatingcompartments_topic-To_edit_your_tenancys_Amazon_S3_Compatibility_API_and_Swift_API_compartment_designations.htm#top
