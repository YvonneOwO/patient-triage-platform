# ============================================================
# Stage 1: Build the application
# ============================================================
FROM eclipse-temurin:21-jdk AS builder

WORKDIR /build

# Copy Maven wrapper and pom.xml
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Make mvnw executable
RUN chmod +x ./mvnw

# Copy source code
COPY src ./src

# Build the application
RUN ./mvnw clean package -DskipTests

# ============================================================
# Stage 2: Run the application
# ============================================================
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy the built jar from builder stage
COPY --from=builder /build/target/*.jar app.jar

# Expose port
EXPOSE 8080

# Run Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]
