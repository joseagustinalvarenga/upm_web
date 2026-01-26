FROM maven:3.9.6-eclipse-temurin-17-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jdk-alpine

COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java", "-Djava.net.preferIPv4Stack=true", "-jar", "/app.jar"]
