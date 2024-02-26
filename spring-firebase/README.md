# spring-firebase

## sdk 인증 초기화

### 서비스 계정 키 경로를 명시적으로 전달

```kotlin
val options = FirebaseOptions.builder()
  .setCredentials(GoogleCredentials.fromStream(FileInputStream("GOOGLE_CREDENTIAL_PATH")))
  .build()

FirebaseApp.initializeApp(options)
```

### 서비스 계정 환경변수에 지정후 자동 처리(권장)

- 서비스 계정키 경로를 환경변수로 지정, **GOOGLE_APPLICATION_CREDENTIALS**

```kotlin
val options = FirebaseOptions.builder()
  .setCredentials(GoogleCredentials.getApplicationDefault())
  .build()

FirebaseApp.initializeApp(options)
```

참조: [Google 이외의 환경에서 SDK 초기화](https://firebase.google.com/docs/admin/setup?hl=ko#initialize_the_sdk_in_non-google_environments)

## Firebase Cloud Messaging(FCM)

- 메시지를 안정적으로 전송할 수 있는 크로스 플랫폼 메시징 솔루션
  - ios, adroid, flutter, web 등 다양한 플랫폼 지원
- [fcm introduction](https://firebase.google.com/docs/cloud-messaging?hl=ko)
