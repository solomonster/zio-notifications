package com.ismometrical.notifications
import com.ismometrical.notification.infrastructure.{KafkaNotificationConsumer, SMSSender}
import com.ismometrical.notification.service.NotificationServiceLive
import com.ismometrical.notifications.domain.Channel
import com.ismometrical.notifications.infrastructure.EmailSender
import zio._
import zio.kafka.consumer._

object Main extends ZIOAppDefault {
  val emailApiKey = sys.env("SENDGRID_API_KEY")
  val emailFrom = sys.env("EMAIL_FROM")

  val twilioSid = sys.env("TWILIO_ACCOUNT_SID")
  val twilioToken = sys.env("TWILIO_AUTH_TOKEN")
  val smsFrom = sys.env("TWILIO_FROM_NUMBER")

  val emailSender: Channel = EmailSender(emailApiKey, emailFrom)
  val smsSender: Channel = SMSSender(twilioSid, twilioToken, smsFrom)
  val notificationService = NotificationServiceLive(emailSender, smsSender)

  // ✅ Run method with proper layer injection
  //override def run: ZIO[Scope, Throwable, Unit] =
    //KafkaNotificationConsumer
      //.run(notificationService)
      //.provideLayer(KafkaNotificationConsumer.consumerLayer)
      //.provideSomeLayer[Scope](KafkaNotificationConsumer.consumerLayer)

  override def run: ZIO[Any with ZIOAppArgs with Scope, Throwable, Unit] =
    KafkaNotificationConsumer.consumeStream(notificationService)
      .runDrain
      .provideLayer(KafkaNotificationConsumer.consumerLayer)
}
