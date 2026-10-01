# Compila com o Maven Wrapper e Java 21.
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /build

# Copia o Maven Wrapper e o arquivo pom.xml para a imagem de build.
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw

# Copia o código-fonte para a imagem de build.
COPY src/ src/
# Os testes com PostgreSQL são executados separadamente no CI.
RUN ./mvnw --batch-mode -DskipTests package

# A imagem final contém somente o Java necessário para executar o JAR.
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copia o JAR gerado na imagem de build para a imagem final, definindo o usuário e grupo como 10001.
COPY --from=build --chown=10001:10001 /build/target/*.jar /app/app.jar
USER 10001:10001

# Define a porta que o aplicativo irá expor.
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
