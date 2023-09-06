FROM openjdk:22-ea-slim

WORKDIR /dir

COPY target/*.jar /dir/library-api.jar

ENV APP_PORT 8080
EXPOSE $APP_PORT

CMD ["java", "-jar", "library-api.jar"]
