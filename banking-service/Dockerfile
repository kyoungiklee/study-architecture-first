# Multi-stage Dockerfile for banking-service (Spring Boot, Java 21)

FROM eclipse-temurin:21-jdk AS builder
WORKDIR /workspace

# Copy Gradle wrapper and settings
COPY gradlew gradlew.bat settings.gradle build.gradle gradle.properties ./
COPY gradle ./gradle

# Pre-download Gradle (leverage cache)
RUN ./gradlew --version --no-daemon

# Copy only required modules to leverage Docker layer cache
COPY common ./common
COPY banking-service ./banking-service

# Build only banking-service jar (skip tests to speed up image build)
RUN ./gradlew :banking-service:bootJar -x test --no-daemon


FROM eclipse-temurin:21-jre AS runner
WORKDIR /app

# Copy the built jar from the builder stage
COPY --from=builder /workspace/banking-service/build/libs/*.jar /app/app.jar

# Default profile/port can be overridden by environment variables
ENV SPRING_PROFILES_ACTIVE=local \
    SERVER_PORT=8082 \
    JAVA_TOOL_OPTIONS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

EXPOSE 8082

# banking-service has server.port=0 in application.yml, so we override via CLI
ENTRYPOINT ["sh", "-c", "exec java -jar /app/app.jar --server.port=${SERVER_PORT} --spring.profiles.active=${SPRING_PROFILES_ACTIVE}"]
