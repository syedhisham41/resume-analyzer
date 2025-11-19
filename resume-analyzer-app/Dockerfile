# Stage 1: Build the JAR

FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run the JAR

FROM eclipse-temurin:21-jdk

WORKDIR /resume-analyzer
COPY --from=builder /app/target/resume-analyzer.jar .
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "resume-analyzer.jar"]

