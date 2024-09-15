#!/bin/bash

# Afficher le répertoire courant pour vérification
echo "Répertoire actuel : $(pwd)"

# Aller dans le répertoire du projet Angular (frontend)
if [ -d "frontend" ]; then
  cd frontend
  npm install
  npm run build
  echo "Build front angular terminé"
  ng test
  cd ..
else
  echo "Le répertoire frontend n'existe pas."
  exit 1
fi

# Aller dans le répertoire du projet Spring Boot (backend)
if [ -d "backend" ]; then
  cd backend
  ./mvnw clean install
  ./mvnw spring-boot:run &
  echo "Backend installé et démarré"
  ./mvnw test
  cd ..
else
  echo "Le répertoire backend n'existe pas."
  exit 1
fi
