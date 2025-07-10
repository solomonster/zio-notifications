package com.ismometrical.notifications.infrastructure

import com.ismometrical.notifications.domain.Channel
import com.sendgrid._
import com.sendgrid.helpers.mail.Mail
import com.sendgrid.helpers.mail.objects.{Content, Email, Personalization}
import com.sun.security.ntlm.Client
import zio._
import com.sendgrid.Request

import java.io.IOException

case class EmailSender(apiKey: String, from: String) extends Channel {

  private val client = new SendGrid(apiKey)

  override def send(to: String, message: String): Task[Unit] =
    for {
      mail <- ZIO.succeed {
        val fromEmail = new Email(from)
        val toEmail = new Email(to)
        val mail = new Mail()
        mail.setFrom(fromEmail)
        mail.setSubject("Notification")
        mail.addContent(new Content("text/plain", message))
        val personalization = new Personalization()
        personalization.addTo(toEmail)
        mail.addPersonalization(personalization)
        mail
      }

      request <- ZIO.succeed {
        val req = new Request()
        req.setMethod(Method.POST)
        req.setEndpoint("mail/send")
        req.setBody(mail.build())
        req
      }

      response <- ZIO.attemptBlocking(client.api(request))

      _ <- if (response.getStatusCode >= 200 && response.getStatusCode < 300)
        ZIO.unit
      else
        ZIO.fail(new IOException(s"SendGrid failed with code ${response.getStatusCode}"))
    } yield ()

}

