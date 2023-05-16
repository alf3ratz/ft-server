#FROM openjdk:17-jdk-alpine
#ARG JAR_FILE=target/*.jar
#RUN ls
#COPY ./target/ft-server-0.0.1-SNAPSHOT.jar app.jar
#ENTRYPOINT ["java","-jar","/app.jar"]
FROM maven:3.8.3-openjdk-11-slim AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:11-jre-slim
COPY --from=build /app/target/*.jar /myapp.jar
EXPOSE 8080
CMD ["java", "-jar", "/myapp.jar"]