# Build stage
# ---- build stage ----
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace

COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline

COPY src ./src
RUN mvn -q -DskipTests clean package -DskipExec=true

# Run stage
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy JAR
COPY --from=build /workspace/target/user-service-0.1.0.jar ./user-service.jar

# Copy configuration
COPY src/main/resources/config.yaml ./config.yaml

# Expose port as defined in config.yaml
EXPOSE 8083

# Run JAR with explicit config
CMD ["java", "-jar", "user-service.jar"]
