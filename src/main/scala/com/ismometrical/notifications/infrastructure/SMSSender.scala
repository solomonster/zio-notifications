package com.ismometrical.notification.infrastructure

import com.ismometrical.notifications.domain.Channel
import com.twilio.Twilio
import com.twilio.rest.api.v2010.account.Message
import com.twilio.`type`.PhoneNumber
import zio._

case class SMSSender(accountSid: String, authToken: String, from: String) extends Channel {

  override def send(to: String, message: String): Task[Unit] = ZIO.attemptBlocking {
    Twilio.init(accountSid, authToken)
    Message.creator(
      new PhoneNumber(to),
      new PhoneNumber(from),
      message
    ).create()
    ()
  }
}

