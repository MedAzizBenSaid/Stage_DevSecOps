# ==========================
# Étape 1 : Compilation
# ==========================
FROM maven:3.9.9-eclipse-temurin-17 AS build

# Définir le répertoire de travail
WORKDIR /app

# Copier le projet
COPY . .

# Compiler le projet et générer le fichier JAR
RUN mvn clean package -DskipTests

# ==========================
# Étape 2 : Image finale
# ==========================
FROM eclipse-temurin:17-jre

# Définir le répertoire de travail
WORKDIR /app

# Copier le fichier JAR depuis l'étape de compilation
COPY --from=build /app/target/*.jar app.jar

# Exposer le port utilisé par Spring Boot
EXPOSE 8081

# Lancer l'application
ENTRYPOINT ["java","-Dspring.profiles.active=docker","-jar","app.jar"]