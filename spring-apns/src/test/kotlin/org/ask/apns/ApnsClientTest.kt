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

    runCatching {
      apnsClient.sendNotification(pushNotification).get()
    }.onSuccess {
      if (it.isAccepted) {
        println("Push notification accepted by APNs gateway.")
      } else {
        println("Notification rejected by the APNs gateway: ${it.rejectionReason}")
        it.tokenInvalidationTimestamp.ifPresent { timestamp ->
          println("\t…and the token is invalid as of $timestamp")
        }
      }
    }
  }
}
