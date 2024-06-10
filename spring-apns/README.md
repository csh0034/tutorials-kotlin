# spring-apns

## pushy

- Pushy is a Java library for sending APNs (iOS, macOS, and Safari) push notifications.
- Pushy sends push notifications using Apple's HTTP/2-based APNs protocol
- https://github.com/jchambers/pushy

### gradle

```kotlin
// https://mvnrepository.com/artifact/com.eatthepath/pushy
implementation("com.eatthepath:pushy:0.15.4")
```

### Troubleshooting

#### No route to host

- https://github.com/jchambers/pushy/issues/1044
- `-Djava.net.preferIPv4Stack=true` jvm 옵션 추가
- apple engineer 답변
  - https://developer.apple.com/forums/thread/741313
  - 23.11 답변, apple 은 최근 IPv6 를 활성화 했으며 푸시 알림서버에 AAAA 레코드를 발표했다.  
    해당 로그는 `netty가 IPv6 사용을 시도하지만 귀하의 네트워크가 이를 지원하지 않는다` 이므로  
    `-Djava.net.preferIPv4Stack=true` 설정을 통해 IPv6 기본 설정을 비활성화해 볼 수 있다.
