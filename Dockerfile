FROM eclipse-temurin:21-jdk
COPY target/testing.jar testing.jar
EXPOSE 9090
ENTRYPOINT ["java", "-jar", "/testing.jar"]