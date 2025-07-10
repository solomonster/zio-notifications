FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/universal/stage/ ./app

CMD ["./app/bin/zio-notifications"]
