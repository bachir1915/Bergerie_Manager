# Image de base : Charge l'environnement Java 17 (version légère alpine)
FROM eclipse-temurin:17-jdk-alpine

# Définit le dossier de travail à l'intérieur du conteneur
WORKDIR /app

# Copie le fichier JAR compilé depuis le dossier target de Windows vers le conteneur
# On le renomme 'app.jar' pour simplifier la commande de démarrage
COPY target/task-api-0.0.1-SNAPSHOT.jar app.jar

# Informe Docker que l'application écoute sur le port 8085
EXPOSE 8085

# Commande exécutée au démarrage du conteneur pour lancer l'application
ENTRYPOINT ["java", "-jar", "app.jar"]