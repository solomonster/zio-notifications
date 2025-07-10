package com.ismometrical.notification.service

import com.ismometrical.notifications.domain.{Channel, NotificationEvent, NotificationType}
import com.ismometrical.notifications.service.NotificationService
import zio._

/**
 * Implementation of the NotificationService that handles Email and SMS.
 */
final case class NotificationServiceLive(emailChannel: Channel, smsChannel: Channel) extends NotificationService {

  override def send(notification: NotificationEvent): IO[Throwable, Unit] =
    notification.notificationType match {
      case NotificationType.Email =>
        emailChannel.send(notification.recipient, notification.message)

      case NotificationType.SMS =>
        smsChannel.send(notification.recipient, notification.message)
    }
}

object NotificationServiceLive {
  /**
   * ZIO Layer for dependency injection (for `provide`).
   */
  def layer(email: Channel, sms: Channel): ULayer[NotificationService] =
    ZLayer.succeed(NotificationServiceLive(email, sms))
}

