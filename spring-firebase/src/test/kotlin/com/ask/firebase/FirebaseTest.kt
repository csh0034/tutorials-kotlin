package com.ask.firebase

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.AndroidConfig
import com.google.firebase.messaging.ApnsConfig
import com.google.firebase.messaging.Aps
import com.google.firebase.messaging.ApsAlert
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import com.google.firebase.messaging.WebpushConfig
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.slf4j.LoggerFactory
import java.io.FileInputStream
import java.time.Instant

private const val GOOGLE_CREDENTIAL_PATH = "enter your credential path"
private const val TEST_FCM_TOKEN = "enter your token"
private val jacksonObjectMapper = jacksonObjectMapper()

/**
 * @see <a href="https://firebase.google.com/docs/reference/fcm/rest/v1/projects.messages">fcm json</a>
 */
@Disabled
class FirebaseTest {
  private val log = LoggerFactory.getLogger(javaClass)

  init {
    if (FirebaseApp.getApps().isEmpty()) {
      val options = FirebaseOptions.builder()
        .setCredentials(GoogleCredentials.fromStream(FileInputStream(GOOGLE_CREDENTIAL_PATH)))
        .build()
      FirebaseApp.initializeApp(options)
    }
  }

  @Test
  fun `Firebase Instance 초기화 검증`() {
    val instance = FirebaseApp.getInstance()
    assertThat(instance).isNotNull
  }

  @Nested
  inner class FcmTest {
    @Test
    fun `notification 만 전송, 공통`() {
      val instance = FirebaseMessaging.getInstance()

      val message = Message.builder()
        .setNotification(
          Notification.builder()
            .setTitle("test notification title")
            .setBody("test notification body")
            .build()
        )
        .setToken(TEST_FCM_TOKEN)
        .build()

      val messageId = instance.send(message).also {
        log.info("messageId: $it")
      }
      assertThat(messageId).isNotNull
    }

    @Test
    fun `data 만 전송, 공통`() {
      val instance = FirebaseMessaging.getInstance()

      val payload = jacksonObjectMapper.writeValueAsString(mapOf("aa" to "bb"))

      val message = Message.builder()
        .putAllData(mapOf("title" to "test data title", "body" to "test data body", "payload" to payload))
        .setToken(TEST_FCM_TOKEN)
        .build()

      val messageId = instance.send(message).also {
        log.info("messageId: $it")
      }
      assertThat(messageId).isNotNull
    }

    /**
     * @see <a href="https://firebase.google.com/docs/cloud-messaging/concept-options?hl=ko#setting-the-priority-of-a-message">fcm priority</a>
     */
    @Test
    fun `data 만 전송, WebPush, 우선순위 high 지정`() {
      val instance = FirebaseMessaging.getInstance()

      val message = Message.builder()
        .putAllData(mapOf("title" to "test data title", "body" to "test data body"))
        .setWebpushConfig(
          WebpushConfig.builder()
            .putHeader("Urgency", "high")
            .build()
        )
        .setToken(TEST_FCM_TOKEN)
        .build()

      val messageId = instance.send(message).also {
        log.info("messageId: $it")
      }
      assertThat(messageId).isNotNull
    }

    /**
     * @see <a href="https://firebase.google.com/docs/cloud-messaging/concept-options?hl=ko#collapsible_and_non-collapsible_messages">fcm collapsible and non-collapsible messages</a>
     */
    @ParameterizedTest
    @ValueSource(strings = ["collapse-id1", "collapse-id1", "collapse-id2", "collapse-id2"])
    fun `notification 만 전송, WebPush, collapsible messages`(collapseId: String) {
      val instance = FirebaseMessaging.getInstance()

      val message = Message.builder()
        .setNotification(
          Notification.builder()
            .setTitle("test notification title")
            .setBody("test notification body")
            .build()
        )
        .setWebpushConfig(
          WebpushConfig.builder()
            .putHeader("Topic", collapseId)
            .build()
        )
        .setToken(TEST_FCM_TOKEN)
        .build()

      val messageId = instance.send(message).also {
        log.info("messageId: $it")
      }
      assertThat(messageId).isNotNull
    }

    @Test
    fun `각 플랫폼에 맞춰 fcm 전송, collapsible messages, ttl, priority`() {
      val instance = FirebaseMessaging.getInstance()
      val collapseId = "test-collapseId"
      val ttl = 3600L
      val title = "test title"
      val body = "test body"
      val content = mapOf("type" to "SAMPLE_TYPE", "payload" to mapOf("payloadKey1" to "payload"))
      val contentJson = jacksonObjectMapper.writeValueAsString(content)

      val message = Message.builder()
        .setAndroidConfig(
          AndroidConfig.builder()
            .setCollapseKey(collapseId)
            .setTtl(ttl * 1000)
            .setPriority(AndroidConfig.Priority.HIGH)
            .putData("title", title)
            .putData("body", body)
            .putData("content", contentJson)
            .build()
        )
        .setApnsConfig(
          ApnsConfig.builder()
            .putHeader("apns-collapse-id", collapseId)
            .putHeader("apns-expiration", Instant.now().plusSeconds(ttl).epochSecond.toString())
            .putHeader("apns-priority", "10")
            .putCustomData("content", contentJson)
            .setAps(
              Aps.builder()
                .setAlert(
                  ApsAlert.builder()
                    .setTitle(title)
                    .setBody(body)
                    .build()
                )
                .build()
            )
            .build()
        )
        .setWebpushConfig(
          WebpushConfig.builder()
            .putHeader("Topic", collapseId)
            .putHeader("TTL", ttl.toString())
            .putHeader("Urgency", "high")
            .putData("title", title)
            .putData("body", body)
            .putData("content", contentJson)
            .build()
        )
        .setToken(TEST_FCM_TOKEN)
        .build()

      val messageId = instance.send(message).also {
        log.info("messageId: $it")
      }
      assertThat(messageId).isNotNull
    }
  }
}
