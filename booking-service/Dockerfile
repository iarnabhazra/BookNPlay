FROM eclipse-temurin:21-jdk as build
WORKDIR /workspace
COPY . /workspace
RUN ./mvnw -q -DskipTests package || mvn -q -DskipTests package

FROM eclipse-temurin:21-jre as runtime
WORKDIR /app
COPY --from=build /workspace/target/booking-service-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]
