FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/user-service-1.0.0.jar ./user-service.jar
EXPOSE 8083
CMD ["java", "-jar", "user-service.jar"]