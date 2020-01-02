FROM openjdk:8-jre-alpine
MAINTAINER Lei
ADD target/MServiceAnalyser-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","app.jar"]