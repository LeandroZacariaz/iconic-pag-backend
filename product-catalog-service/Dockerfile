# Etapa 1: Compilar el proyecto
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Etapa 2: Imagen liviana con solo el .jar
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/product-catalog-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-Xms64m", "-Xmx256m", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=50.0", "-jar", "app.jar"]
