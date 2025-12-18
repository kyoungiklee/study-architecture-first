# Multi-stage Dockerfile for money-service (Spring Boot, Java 21)

FROM eclipse-temurin:21-jdk AS builder
WORKDIR /workspace

# Copy Gradle wrapper and settings
COPY gradlew gradlew.bat settings.gradle build.gradle gradle.properties ./
COPY gradle ./gradle

# Pre-download Gradle and dependencies if possible (this layer will be cached)
RUN ./gradlew --version --no-daemon

# Copy only required modules
COPY common ./common
COPY money-service ./money-service

# Build only money-service jar
RUN ./gradlew :money-service:bootJar -x test --no-daemon

FROM eclipse-temurin:21-jre AS runner
WORKDIR /app

# Copy the built jar
COPY --from=builder /workspace/money-service/build/libs/*.jar /app/app.jar

# Default settings
ENV SPRING_PROFILES_ACTIVE=local \
    SERVER_PORT=8083 \
    JAVA_TOOL_OPTIONS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

EXPOSE 8083

ENTRYPOINT ["sh", "-c", "exec java -jar /app/app.jar --server.port=${SERVER_PORT} --spring.profiles.active=${SPRING_PROFILES_ACTIVE}"]
