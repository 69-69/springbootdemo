FROM openjdk:22-ea-slim

WORKDIR /dir

COPY target/*.jar /dir/app.jar

ENV APP_PORT 8080
EXPOSE $APP_PORT

CMD ["java", "-jar", "app.jar"]