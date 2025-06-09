#base image with Maven and JDK 17
FROM maven:3.9.8-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml ./
COPY src ./src

RUN mvn clean package

FROM eclipse-temurin:17-alpine

#copy jar from build stage to final image, using a wildcard to avoid hardcoding the name
COPY --from=build /app/target/*.jar app.jar

# Expose port
EXPOSE 9082
#command line to run jar
ENTRYPOINT ["java","-jar","/app.jar"]
