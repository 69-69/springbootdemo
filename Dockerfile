FROM openjdk:17-jdk

WORKDIR /dir

COPY target/*.jar /dir/app.jar

ENV APP_PORT 8080
EXPOSE $APP_PORT

CMD ["java", "-jar", "app.jar"]