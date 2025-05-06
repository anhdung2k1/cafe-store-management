# Use official Maven image to build the application
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim

ARG COMMIT
ARG APP_VERSION
ARG BUILD_TIME

LABEL \
    app.commit=$COMMIT \
    app.version=$APP_VERSION \
    image.title="Backend Authentication Service" \
    image.created="$BUILD_TIME"

WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 9090

CMD ["java", "-jar", "app.jar"]