# 1. Fase de compilación (Maven + Java 21)
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
# Copiamos el pom.xml y el código fuente
COPY pom.xml .
COPY src ./src
# Compilamos el proyecto saltando los tests para ahorrar tiempo en el deploy
RUN mvn clean package -DskipTests

# 2. Fase de ejecución (Solo el JRE para que sea más ligero)
FROM eclipse-temurin:21-jdk
WORKDIR /app
# Copiamos el archivo .jar generado en la fase anterior
COPY --from=build /app/target/*.jar app.jar
# Exponemos el puerto (Render usará el que definas en sus variables)
EXPOSE 8080
# Comando para arrancar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]