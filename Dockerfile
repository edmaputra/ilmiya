# Multi-stage Dockerfile for Ilmiya Application
# Stage 1: Build
FROM eclipse-temurin:25-jdk AS builder
WORKDIR /workspace

# Copy Gradle wrapper and configuration files first for better caching
COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle.kts settings.gradle.kts gradle.properties ./
COPY ilmiya-common/build.gradle.kts ilmiya-common/
COPY ilmiya-core/build.gradle.kts ilmiya-core/
COPY ilmiya-academic/build.gradle.kts ilmiya-academic/
COPY ilmiya-student/build.gradle.kts ilmiya-student/
COPY ilmiya-attendance/build.gradle.kts ilmiya-attendance/
COPY ilmiya-hr/build.gradle.kts ilmiya-hr/
COPY ilmiya-timetable/build.gradle.kts ilmiya-timetable/
COPY ilmiya-exam/build.gradle.kts ilmiya-exam/
COPY ilmiya-finance/build.gradle.kts ilmiya-finance/
COPY ilmiya-app/build.gradle.kts ilmiya-app/

RUN chmod +x gradlew && ./gradlew dependencies --no-daemon || true

# Copy all source files and compile bootJar
COPY . .
RUN ./gradlew :ilmiya-app:bootJar -x test --no-daemon

# Stage 2: Runtime
FROM eclipse-temurin:25-jre AS runner
WORKDIR /app

# Create a dedicated non-root user
RUN addgroup --system appgroup && adduser --system --ingroup appgroup appuser
USER appuser:appgroup

COPY --from=builder /workspace/ilmiya-app/build/libs/*.jar app.jar

ENV SERVER_PORT=8080
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
