package com.ask.firebase.config

import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import javax.annotation.PostConstruct

//@Configuration
class FirebaseConfig {
  @PostConstruct
  fun init() {
    if (FirebaseApp.getApps().isEmpty()) {
      initFirebaseApp()
    }
  }

  private fun initFirebaseApp() {
    val options = FirebaseOptions.builder()
//      .setCredentials(GoogleCredentials.fromStream(FileInputStream("GOOGLE_CREDENTIAL_PATH"))) 1. 직접 path 지정
//      .setCredentials(GoogleCredentials.getApplicationDefault()) 2. 환경변수로 path 지정 (권장)
      .build()
    FirebaseApp.initializeApp(options)
  }
}
