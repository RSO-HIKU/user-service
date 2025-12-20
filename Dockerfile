FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy JAR
COPY target/user-service-0.1.0.jar ./user-service.jar

# Copy configuration
COPY src/main/resources/config.yaml ./config.yaml

# Expose port as defined in config.yaml
EXPOSE 8083

# Run JAR with explicit config
CMD ["java", "-jar", "user-service.jar"]
