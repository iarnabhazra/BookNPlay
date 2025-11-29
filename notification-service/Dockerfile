FROM eclipse-temurin:21-jre
ARG JAR=target/notification-service-0.0.1-SNAPSHOT.jar
WORKDIR /app
COPY ${JAR} app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]
