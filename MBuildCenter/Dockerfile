FROM openjdk:8-jre-alpine
MAINTAINER Lei
ADD target/MBuildCenter-1.0-SNAPSHOT.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","app.jar"]