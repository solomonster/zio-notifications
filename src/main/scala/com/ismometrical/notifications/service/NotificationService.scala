package com.ismometrical.notifications.service

import com.ismometrical.notifications.domain.{Channel, NotificationEvent, NotificationType}
import zio.Task

trait NotificationService {
  def send(event: NotificationEvent): Task[Unit]
}
