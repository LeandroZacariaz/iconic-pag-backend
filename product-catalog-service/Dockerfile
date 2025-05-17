FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY target/product-catalog-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-Xms64m", "-Xmx256m", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=50.0", "-jar", "app.jar"]
