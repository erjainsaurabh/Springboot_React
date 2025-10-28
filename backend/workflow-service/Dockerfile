# ---- Build stage ----
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy only the pom first so deps can be cached
COPY pom.xml .

# Pre-fetch dependencies into a reusable layer
# BuildKit will persist ~/.m2 between builds
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -DskipTests dependency:go-offline

# Now copy sources and compile
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -DskipTests package

# ---- Runtime stage (small) ----
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copy the built jar from the builder
ARG JAR_FILE=/app/target/*-SNAPSHOT.jar
COPY --from=build ${JAR_FILE} app.jar

# Create logs directory
RUN mkdir -p /app/logs

EXPOSE 8082
ENTRYPOINT ["java","-jar","/app/app.jar"]
