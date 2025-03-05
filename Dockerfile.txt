# Use a minimal JDK 21 image for building the application
FROM eclipse-temurin:21-jdk AS build

# Set working directory
WORKDIR /app

# Copy necessary files for Maven build
COPY pom.xml mvnw ./
COPY .mvn .mvn/

# Ensure the Maven wrapper has execute permissions and download dependencies
RUN chmod +x mvnw && ./mvnw dependency:go-offline

# Debug: List files to verify that the build context contains expected files
RUN echo "Listing files in /app after copying pom.xml, mvnw, and .mvn:" && ls -la /app

# Copy the source code explicitly from the repository root into /app/src/
COPY ./src ./src/

# Debug: Verify that the src directory was copied successfully
RUN echo "Listing contents of /app/src:" && ls -la /app/src

# Build the application
RUN ./mvnw clean package -DskipTests

# Use a lightweight JRE image for running the application
FROM eclipse-temurin:21-jre

# Set working directory
WORKDIR /app

# Copy the built JAR from the build stage (using wildcard to avoid hardcoding the jar name)
COPY --from=build /app/target/*.jar app.jar

# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
