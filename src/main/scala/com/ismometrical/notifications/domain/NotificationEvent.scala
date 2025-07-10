package com.ismometrical.notifications.domain

import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}

sealed trait NotificationType
object NotificationType {
  case object Email extends NotificationType
  case object SMS extends NotificationType

  implicit val decoder: JsonDecoder[NotificationType] = DeriveJsonDecoder.gen[NotificationType]
  implicit val encoder: JsonEncoder[NotificationType] = DeriveJsonEncoder.gen[NotificationType]
}

case class NotificationEvent
(
  recipient: String,
  message: String,
  notificationType: NotificationType
)

object NotificationEvent {

  implicit val decoder: JsonDecoder[NotificationEvent] = DeriveJsonDecoder.gen[NotificationEvent]
  implicit  val encoder: JsonEncoder[NotificationEvent] = DeriveJsonEncoder.gen[NotificationEvent]
}
