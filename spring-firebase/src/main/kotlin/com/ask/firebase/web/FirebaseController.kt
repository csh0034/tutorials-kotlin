package com.ask.firebase.web

import com.google.auth.oauth2.ServiceAccountCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.ImplFirebaseTrampolines
import org.springframework.http.HttpHeaders
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClient

@RestController
class FirebaseController(
  private val webClient: WebClient,
) {
  @PostMapping("/fcm")
  fun sendFcm(@RequestBody json: String): String {
    val credentials = ImplFirebaseTrampolines.getCredentials(FirebaseApp.getInstance()) as ServiceAccountCredentials
    val accessToken = credentials.refreshAccessToken()

    return webClient.post()
      .uri("https://fcm.googleapis.com/v1/projects/${credentials.projectId}/messages:send")
      .header(HttpHeaders.AUTHORIZATION, "Bearer ${accessToken.tokenValue}")
      .header(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
      .bodyValue("{\"validate_only\": false, \"message\": $json}")
      .retrieve()
      .bodyToMono(String::class.java)
      .block() ?: "empty"
  }
}
