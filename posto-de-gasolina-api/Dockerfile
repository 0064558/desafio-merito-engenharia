# Compila com o Maven Wrapper e Java 21.
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /build

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw

COPY src/ src/
# Os testes com PostgreSQL são executados separadamente no CI.
RUN ./mvnw --batch-mode -DskipTests package

# A imagem final contém somente o Java necessário para executar o JAR.
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

COPY --from=build --chown=10001:10001 /build/target/*.jar /app/app.jar
USER 10001:10001

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
