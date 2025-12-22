# Multi-stage Dockerfile for payment-service (Spring Boot, Java 21)

FROM eclipse-temurin:21-jdk AS builder
WORKDIR /workspace

# Copy Gradle wrapper and settings
COPY gradlew gradlew.bat settings.gradle build.gradle gradle.properties ./
COPY gradle ./gradle

# Pre-download Gradle (leverage cache)
RUN ./gradlew --version --no-daemon

# Copy only required modules
COPY common ./common
COPY payment-service ./payment-service

# Build only payment-service jar
RUN ./gradlew :payment-service:bootJar -x test --no-daemon

FROM eclipse-temurin:21-jre AS runner
WORKDIR /app

# Copy the built jar
COPY --from=builder /workspace/payment-service/build/libs/*.jar /app/app.jar

# Default settings
ENV SPRING_PROFILES_ACTIVE=local \
    SERVER_PORT=8086 \
    JAVA_TOOL_OPTIONS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

EXPOSE 8086

ENTRYPOINT ["sh", "-c", "exec java -jar /app/app.jar --server.port=${SERVER_PORT} --spring.profiles.active=${SPRING_PROFILES_ACTIVE}"]
