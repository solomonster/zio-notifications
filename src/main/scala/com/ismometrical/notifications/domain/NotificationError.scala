package com.ismometrical.notifications.domain

sealed trait NotificationError extends Throwable
case object InvalidEmail extends NotificationError
case object DeliveryFailed extends NotificationError

