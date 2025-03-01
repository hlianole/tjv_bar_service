FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/*.jar app.jar

COPY src/main/resources/application-docker.properties /app/config/application-docker.properties

CMD ["java", "-jar", "app.jar"]