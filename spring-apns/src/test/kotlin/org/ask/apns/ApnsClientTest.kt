package org.ask.apns

import com.eatthepath.pushy.apns.ApnsClientBuilder
import com.eatthepath.pushy.apns.DeliveryPriority
import com.eatthepath.pushy.apns.PushType
import com.eatthepath.pushy.apns.util.SimpleApnsPayloadBuilder
import com.eatthepath.pushy.apns.util.SimpleApnsPushNotification
import com.eatthepath.pushy.apns.util.TokenUtil
import org.junit.jupiter.api.Test
import java.io.File
import java.time.Instant

private const val APNS_CREDENTIAL_PATH = ""
private const val APNS_PASSWORD = ""
private const val APNS_BUNDLE_ID = ""
private const val APNS_TOKEN = ""

class ApnsClientTest {
  init {
    System.setProperty("java.net.preferIPv4Stack", "true")
  }

  @Test
  fun `voip 메세지 전송`() {
    val apnsClient = ApnsClientBuilder()
      .setApnsServer(ApnsClientBuilder.DEVELOPMENT_APNS_HOST)
      .setClientCredentials(File(APNS_CREDENTIAL_PATH), APNS_PASSWORD)
      .build()

    val payload = SimpleApnsPayloadBuilder()
      .addCustomProperty("type", "call")
      .addCustomProperty("payload", "{\"spaceId\": \"space01\", \"roomId\": \"room01\"}")
      .build()
    val token = TokenUtil.sanitizeTokenString(APNS_TOKEN)
    val topic = "$APNS_BUNDLE_ID.voip"

    val pushNotification = SimpleApnsPushNotification(
      token,
      topic,
      payload,
      Instant.now().plus(SimpleApnsPushNotification.DEFAULT_EXPIRATION_PERIOD),
      DeliveryPriority.IMMEDIATE,
      PushType.VOIP
    )

    apnsClient.sendNotification(pushNotification)
      .whenComplete { response, cause ->
        if (response != null) {
          println("[${Thread.currentThread().name}] $response")

          if (response.isAccepted) {
            println("Push notification accepted by APNs gateway.")
          } else {
            println("Notification rejected by the APNs gateway: ${response.rejectionReason}")
            response.tokenInvalidationTimestamp.ifPresent { timestamp ->
              println("the token is invalid as of $timestamp")
            }
          }
        } else {
          // Something went wrong when trying to send the notification to the
          // APNs server. Note that this is distinct from a rejection from
          // the server, and indicates that something went wrong when actually
          // sending the notification or waiting for a reply.
          cause.printStackTrace()
        }
      }.get() // 비동기 처리를 위해선 get() 을 호출하지 않으면 된다.
  }
}
