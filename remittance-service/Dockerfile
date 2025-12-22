# Multi-stage Dockerfile for remittance-service (Spring Boot, Java 21)

FROM eclipse-temurin:21-jdk AS builder
WORKDIR /workspace

# Copy Gradle wrapper and settings
COPY gradlew gradlew.bat settings.gradle build.gradle gradle.properties ./
COPY gradle ./gradle

# Pre-download Gradle (leverage cache)
RUN ./gradlew --version --no-daemon

# Copy only required modules to leverage Docker layer cache
COPY common ./common
COPY remittance-service ./remittance-service

# Build only remittance-service jar (skip tests to speed up image build)
RUN ./gradlew :remittance-service:bootJar -x test --no-daemon


FROM eclipse-temurin:21-jre AS runner
WORKDIR /app

# Copy the built jar from the builder stage
COPY --from=builder /workspace/remittance-service/build/libs/*.jar /app/app.jar

# Default profile/port can be overridden by environment variables
ENV SPRING_PROFILES_ACTIVE=local \
    SERVER_PORT=8084 \
    JAVA_TOOL_OPTIONS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

EXPOSE 8084

# Override via CLI if necessary
ENTRYPOINT ["sh", "-c", "exec java -jar /app/app.jar --server.port=${SERVER_PORT} --spring.profiles.active=${SPRING_PROFILES_ACTIVE}"]
