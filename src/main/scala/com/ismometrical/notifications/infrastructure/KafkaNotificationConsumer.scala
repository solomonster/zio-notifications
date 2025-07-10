package com.ismometrical.notification.infrastructure

import com.ismometrical.notifications.domain.NotificationEvent
import com.ismometrical.notifications.service.NotificationService
import zio._
import zio.json.DecoderOps
import zio.kafka.consumer._
import zio.kafka.serde._
import zio.stream.ZStream

object KafkaNotificationConsumer {

  val topic = "notifications"
  val bootstrap = "localhost:29092"   // before it was kafka:29092
  val groupId = "notification-service"

  val consumerSettings = ConsumerSettings(List(bootstrap)).withGroupId(groupId)
    .withClientId("notification-consumer")
  //val managedConsumer = Consumer.make(consumerSettings)
 // val consumer = ZLayer.from
  //val consumerLayer: Layer[Any, Throwable, Consumer] =
    //ZLayer.scoped { // (1)
      //val consumerSettings: ConsumerSettings =
        //ConsumerSettings(List("localhost:9092")).withGroupId("group")
      //Consumer.make(consumerSettings)
    //}
  //val consumerLayer: ZLayer[Any, Throwable, Consumer] = ZLayer.scoped(Consumer.make(consumerSettings))

  val consumer: ZIO[Scope, Throwable, Consumer] = Consumer.make(consumerSettings)
  val consumerLayer = ZLayer.scoped(Consumer.make(consumerSettings))

  def consumeStream(notificationService: NotificationService): ZStream[Consumer, Throwable, Nothing] =
    Consumer
      .plainStream(Subscription.topics("notifications"), Serde.string, Serde.string)
      .tap { record =>
        record.value.fromJson[NotificationEvent] match {
          case Right(notification) =>
            notificationService.send(notification)
              .tapError(e => Console.printLineError(s"❌ Failed to send notification: ${e.getMessage}"))
          case Left(error) =>
            Console.printLineError(s"❌ Failed to parse message: $error\nRaw: ${record.value}")
        }
      }
      .map(_.offset)
      .aggregateAsync(Consumer.offsetBatches)
      .mapZIO(_.commit)
      .drain

  def run(notificationService: NotificationService): ZIO[Scope, Throwable, Unit] = {
    Consumer
      .consumeWith(
        settings = consumerSettings,
        subscription = Subscription.topics(topic),
        keyDeserializer = Serde.string,
        valueDeserializer = Serde.string
      ) { record =>
        // 🔄 Deserialize JSON into NotificationEvent
        record.value.fromJson[NotificationEvent] match {
          case Right(event) =>
            notificationService.send(event)
              .catchAll(e => ZIO.logError(s"❌ Failed to send: ${e.getMessage}"))
          case Left(error) =>
            ZIO.logError(s"❌ Failed to decode message: $error")
        }
      }
  }
}

