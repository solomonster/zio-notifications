package com.ismometrical.notifications

import com.ismometrical.notification.infrastructure.{KafkaNotificationConsumer, SMSSender}
import com.ismometrical.notifications.service.NotificationServiceLive
import com.ismometrical.notifications.domain.Channel
import com.ismometrical.notifications.infrastructure.EmailSender
import com.ismometrical.notifications.service.NotificationServiceLive
import io.github.cdimascio.dotenv.Dotenv
import zio._
import zio.kafka.consumer._

object Main extends ZIOAppDefault {

  val dotenv = Dotenv.load()

  val emailApiKey = Option(dotenv.get("SENDGRID_API_KEY"))
    .getOrElse(throw new RuntimeException("SENDGRID_API_KEY not found"))
  val emailFrom = Option(dotenv.get("EMAIL_FROM"))
    .getOrElse(throw new RuntimeException("EMAIL_FROM not found"))
  val twilioSid = Option(dotenv.get("TWILIO_ACCOUNT_SID"))
    .getOrElse(throw new RuntimeException("TWILIO_ACCOUNT_SID not found"))
  val twilioToken = Option(dotenv.get("TWILIO_AUTH_TOKEN"))
    .getOrElse(throw new RuntimeException("TWILIO_AUTH_TOKEN not found"))
  val smsFrom = Option(dotenv.get("TWILIO_FROM_NUMBER"))
    .getOrElse(throw new RuntimeException("TWILIO_FROM_NUMBER not found"))

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
