# Build executable .jar file
FROM maven:3.8.5-openjdk-17 AS build
COPY . .
RUN mvn clean package -DskipTests
# Run .jar
FROM openjdk:17-oracle
COPY --from=build /target/bookti-0.0.1.jar bookti.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "bookti.jar"]