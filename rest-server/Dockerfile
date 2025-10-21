FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/rest-server-*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]