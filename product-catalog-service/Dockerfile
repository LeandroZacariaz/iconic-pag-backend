# Etapa de compilación
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .
# Descargar dependencias primero para aprovechar la caché
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa de ejecución
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/product-catalog-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8082
# Configurar JVM con límites de memoria
ENTRYPOINT ["java", "-Xms64m", "-Xmx256m", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=50.0", "-jar", "app.jar"]