package com.ask.firebase.config

import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import org.springframework.context.annotation.Bean

//@Configuration
class FirebaseConfig {
  @Bean
  fun firebaseMessaging(firebaseApp: FirebaseApp): FirebaseMessaging {
    return FirebaseMessaging.getInstance(firebaseApp)
  }

  @Bean
  fun firebaseApp(): FirebaseApp {
    if (FirebaseApp.getApps().isEmpty()) {
      val options = FirebaseOptions.builder()
//      .setCredentials(GoogleCredentials.fromStream(FileInputStream("GOOGLE_CREDENTIAL_PATH"))) 1. 직접 path 지정
//      .setCredentials(GoogleCredentials.getApplicationDefault()) 2. 환경변수로 path 지정 (권장)
        .build()

      return FirebaseApp.initializeApp(options)
    }

    return FirebaseApp.getInstance()
  }
}
