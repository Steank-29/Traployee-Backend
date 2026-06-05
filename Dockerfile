# Use Eclipse Temurin (modern, supported Java image)
FROM eclipse-temurin:17-jdk-alpine

# Set working directory inside container
WORKDIR /app

# Copy the jar file (adjust path if needed)
COPY target/*.jar app.jar

# Expose your Spring Boot port (default 8080)
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]