# BUILD STAGE
FROM maven:3.8.7-openjdk-18 AS build
WORKDIR /build
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

# RUNTIME STAGE
FROM amazoncorretto:17
ARG APP_VERSION=1.0.0
ARG PROFILE=dev
WORKDIR /app
COPY --from=build /build/target/bookti-*.jar /app/
EXPOSE 8080
ENV JAR_VERSION=${APP_VERSION}
ENV ACTIVE_PROFILE=${PROFILE}
CMD java -jar -Dspring.profiles.active=${ACTIVE_PROFILE} bookti-${JAR_VERSION}.jar
#ENTRYPOINT ["java", "-jar", "bookti.jar"]
