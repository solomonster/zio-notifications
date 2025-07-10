package com.ismometrical.notifications.domain

import zio.Task
trait Channel {
  def send(to: String, message: String): Task[Unit]
}
