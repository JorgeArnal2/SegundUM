FROM maven:3.8.6-eclipse-temurin-11 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B || true
COPY src ./src
# Compile the project
RUN mvn clean compile -B

# We use the maven image to run exec:java since there's no fat jar plugin configured
CMD ["mvn", "exec:java"]
