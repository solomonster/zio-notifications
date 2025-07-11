package com.ismometrical.notifications.service

import com.ismometrical.notifications.domain.{Channel, NotificationEvent, NotificationType}
import zio._

/**
 * Implementation of the NotificationService that handles Email and SMS.
 */
final case class NotificationServiceLive(emailChannel: Channel, smsChannel: Channel) extends NotificationService {

  override def send(notification: NotificationEvent): IO[Throwable, Unit] =
    Console.printLine(s"[DEBUG] Received notification: $notification") *>
      (notification.notificationType match {
      case NotificationType.Email =>
        emailChannel.send(notification.recipient, notification.message)
          .tap(_ => Console.printLine("[DEBUG] Email sent"))

      case NotificationType.SMS =>
        smsChannel.send(notification.recipient, notification.message)
          .tap(_ => Console.printLine("[DEBUG] SMS sent"))
    }).tapError(e => Console.printLine(s"[ERROR] Failed to send notification: ${e.getMessage}"))

}

object NotificationServiceLive {
  /**
   * ZIO Layer for dependency injection (for `provide`).
   */
  def layer(email: Channel, sms: Channel): ULayer[NotificationService] =
    ZLayer.succeed(NotificationServiceLive(email, sms))
}

