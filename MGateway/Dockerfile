FROM openjdk:8-jre-alpine
MAINTAINER Lei
ADD target/MGateway-1.0-SNAPSHOT.jar app.jar
EXPOSE 8001
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom", "-Djava.net.preferIPv4Stack=true", "-jar","app.jar"]