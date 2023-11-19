# Use the official OpenJDK base image
FROM openjdk:22

# Copy the Spring Boot application (assuming the build output folder is ./target)
COPY ./target/week_01_task_01-0.0.1-SNAPSHOT.jar /app.jar

# Set the internal port that the Spring Boot application uses
EXPOSE 8080

# Start the application on container start
CMD ["java", "-jar", "/app.jar"]
