FROM eclipse-temurin:17-jdk
COPY ./build/libs/uga-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]