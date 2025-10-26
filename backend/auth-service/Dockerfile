FROM openjdk:17-jdk-slim

WORKDIR /app

# Install Maven
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

# Copy pom.xml first for better caching
COPY pom.xml .
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "target/auth-service-0.0.1-SNAPSHOT.jar"]
