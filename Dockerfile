# 1. Usa una JDK
FROM eclipse-temurin:17-jdk-jammy

# 2. Copia il jar prodotto da Maven/Gradle
COPY target/spring-security-aop-auth-0.0.1-SNAPSHOT.jar app.jar

# 3. Espone la porta
EXPOSE 8080

# 4. Comando per avviare Spring Boot
ENTRYPOINT ["java", "-jar", "/app.jar"]