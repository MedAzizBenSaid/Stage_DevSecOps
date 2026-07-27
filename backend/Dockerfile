# ==========================
# Étape 1 : Image finale
# ==========================
FROM eclipse-temurin:17-jre

# Définir le répertoire de travail
WORKDIR /app

# Copier le JAR déjà compilé par Jenkins
COPY target/*.jar app.jar

# Exposer le port utilisé par Spring Boot
EXPOSE 8081

# Lancer l'application
ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-jar", "app.jar"]