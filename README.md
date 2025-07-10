# zio-notifications
Notification service through email/sms using ZIO library of the Scala programming language
To run the project you need .env file where your sendgrid api details
and twilio details to send emails and sms is.

docker-compose up -d --build //.  spin all images and build them
// produce a message from kafka  
docker exec -it kafka kafka-console-producer \
--broker-list localhost:29092 \
--topic notifications

then sample json to send
{
"recipient": "ismometrical@gmail.com",
"message": "Hello!",
"notificationType": "Email"
}