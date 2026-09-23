FROM eclipse-temurin:17-jdk-jammy
COPY target/testing.jar testing.jar
EXPOSE 9090
ENTRYPOINT ["java", "-jar", "/testing.jar"]