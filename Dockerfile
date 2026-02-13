# 1. Fase de compilacion (Maven + Java 21)
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Configuramos el entorno para UTF-8 explícitamente
ENV LANG C.UTF-8

COPY pom.xml .
COPY src ./src

# Compilamos saltando tests
RUN mvn clean package -DskipTests

# 2. Fase de ejecucion
FROM eclipse-temurin:21-jdk
WORKDIR /app

# Copiamos el archivo app.jar (nombre definido en <finalName> del pom.xml)
COPY --from=build /app/target/app.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]