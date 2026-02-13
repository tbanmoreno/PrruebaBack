# 1. Fase de compilacion (Maven + Java 21)
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Configuramos el entorno para UTF-8 explícitamente a nivel de OS
ENV LANG=C.UTF-8
ENV LC_ALL=C.UTF-8

COPY pom.xml .
COPY src ./src

# Forzamos la codificación también como propiedad de Maven para evitar MalformedInputException
RUN mvn clean package -Dmaven.test.skip=true -Dproject.build.sourceEncoding=UTF-8

# 2. Fase de ejecucion
FROM eclipse-temurin:21-jdk
WORKDIR /app

# Seteamos el encoding también en la ejecución para que los logs y la lectura de properties sean correctos
ENV LANG=C.UTF-8
ENV LC_ALL=C.UTF-8

# Copiamos el archivo app.jar
COPY --from=build /app/target/app.jar app.jar

EXPOSE 8080

# Añadimos el parámetro de file.encoding al ejecutar el JAR
ENTRYPOINT ["java", "-Dfile.encoding=UTF-8", "-jar", "app.jar"]