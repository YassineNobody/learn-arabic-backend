# Étape 1 : Build de l’application
FROM gradle:8.4-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle clean build -x test

# Étape 2 : Image d'exécution allégée
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

# Lancement de l’application
ENTRYPOINT ["java", "-jar", "app.jar"]
