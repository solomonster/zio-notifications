package com.ismometrical.notifications
import com.ismometrical.notification.infrastructure.{KafkaNotificationConsumer, SMSSender}
import com.ismometrical.notification.service.NotificationServiceLive
import com.ismometrical.notifications.domain.Channel
import com.ismometrical.notifications.infrastructure.EmailSender
import zio._
import zio.kafka.consumer._

object Main extends ZIOAppDefault {
  val emailApiKey = sys.env.getOrElse("SENDGRID_API_KEY", "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")
  val emailFrom = sys.env.getOrElse("EMAIL_FROM", "ismometrical@gmail.com")

  val twilioSid = sys.env.getOrElse("TWILIO_ACCOUNT_SID", "AC7886228c0cd6ed9809b8b0bc4c6e9f30")
  val twilioToken = sys.env.getOrElse("TWILIO_AUTH_TOKEN", "82b2570df3d0e74e0a306ab413cbee6f")
  val smsFrom = sys.env.getOrElse("TWILIO_FROM_NUMBER", "+19388391440")

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
