# Multi-stage Dockerfile for Spring Boot Application

# Stage 1: Build the application
FROM maven:3.9.12-eclipse-temurin-17 AS build

WORKDIR /app

# Copy Maven configuration files first (for layer caching)
COPY pom.xml .
COPY .mvn .mvn

# Download dependencies (this layer will be cached if pom.xml doesn't change)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application (skip tests to match build.sh)
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Copy the built JAR from the build stage
# Using wildcard to handle version changes
COPY --from=build /app/target/springboot-app-*.jar app.jar

# Expose the port (Render will override with $PORT)
EXPOSE 8080

# Set default environment variables (can be overridden at runtime)
ENV PORT=8080

# Run the application
# Note: MONGODB_URI must be provided at runtime via environment variable
# Using exec form with sh -c for proper signal handling and env var expansion
CMD ["sh", "-c", "java -Dserver.port=$PORT -Dspring.data.mongodb.uri=$MONGODB_URI -jar app.jar"]
