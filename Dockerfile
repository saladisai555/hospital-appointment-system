# =========================
# Build Stage
# =========================
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copy Maven configuration first
COPY pom.xml .

# Copy source code
COPY src ./src

# Build the Spring Boot application
RUN mvn clean package -DskipTests


# =========================
# Runtime Stage
# =========================
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy the generated JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Render will use the PORT environment variable.
EXPOSE 8080

# Limit memory because Render Free has limited RAM
ENTRYPOINT ["java", "-Xmx400m", "-jar", "app.jar"]