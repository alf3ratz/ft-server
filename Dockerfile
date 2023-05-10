FROM openjdk:17-jdk-alpine
ARG JAR_FILE=target/*.jar
RUN ls
COPY ./target/ft-server-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]