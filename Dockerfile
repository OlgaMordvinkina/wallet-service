FROM gradle:8.14.3-jdk17 AS build
COPY . /home/gradle/app/
WORKDIR /home/gradle/app
RUN gradle build

FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY --from=build /home/gradle/app/build/libs/wallet-service-0.0.1-SNAPSHOT.jar /app/app.jar
ENV SERVER_PORT=8080
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]

# FROM eclipse-temurin:17-jdk-jammy
# WORKDIR /app
# COPY build/libs/wallet-service-0.0.1-SNAPSHOT.jar app.jar
# ENV SERVER_PORT=8080
# EXPOSE 8080
# ENTRYPOINT ["java","-jar","app.jar"]
