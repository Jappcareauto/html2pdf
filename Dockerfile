# Build stage for Gradle
FROM openjdk:17-slim AS build

# Set the working directory inside the container
WORKDIR /app

# Copy necessary files for Gradle to work
COPY ./src ./src
COPY ./build.gradle.kts ./build.gradle.kts
COPY ./settings.gradle.kts ./settings.gradle.kts
COPY ./gradlew ./gradlew
COPY ./gradlew.bat ./gradlew.bat
COPY ./gradle/wrapper/gradle-wrapper-original.properties ./gradle/wrapper/gradle-wrapper.properties
COPY ./gradle/wrapper/gradle-wrapper.jar ./gradle/wrapper/gradle-wrapper.jar

# Make the gradlew script executable
RUN chmod +x ./gradlew

# Build the JAR file
RUN ./gradlew bootJar

# Runtime stage for the Spring Boot app
FROM openjdk:17-slim AS run

WORKDIR /app
COPY --from=build /app/build/libs/html2pdf-0.0.1-SNAPSHOT.jar /app/app.jar
EXPOSE 8500
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

