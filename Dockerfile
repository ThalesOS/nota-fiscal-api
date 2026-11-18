# Estágio 1: Build da aplicação com Maven e Java 21
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copia pom.xml e baixa dependências para aproveitar cache de camadas
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copia o código-fonte e compila gerando o .jar
COPY src ./src
RUN mvn clean package -DskipTests

# Estágio 2: Imagem final de execução (leve e otimizada)
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copia o artefato .jar gerado no estágio anterior
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-Djava.awt.headless=true", "-jar", "app.jar"]
