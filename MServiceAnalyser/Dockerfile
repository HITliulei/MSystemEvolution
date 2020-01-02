FROM openjdk:8-jre-alpine
MAINTAINER Lei
ADD MServiceAnalyser/target/MServiceAnalyser-1.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","app.jar"]