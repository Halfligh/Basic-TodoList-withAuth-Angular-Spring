#!/bin/bash

# Variables de suivi des erreurs
frontend_tests_status="SUCCESS"
backend_tests_status="SUCCESS"

# Afficher le répertoire courant pour vérification
echo "Répertoire actuel : $(pwd)"

# Aller dans le répertoire du projet Angular (frontend)
if [ -d "frontend" ]; then
  cd frontend
  npm install
  npm run build
  echo "Build front Angular terminé"
  
  # Exécuter les tests Angular en utilisant la version locale d'Angular CLI
  ./node_modules/.bin/ng test --watch=false
  if [ $? -ne 0 ]; then
    echo "Les tests front-end ont échoué."
    frontend_tests_status="FAILURE"
  else
    echo "Les tests front-end ont réussi !"
  fi
  cd ..
else
  echo "Le répertoire frontend n'existe pas."
  exit 1
fi

# Aller dans le répertoire du projet Spring Boot (backend)
if [ -d "backend" ]; then
  cd backend
  ./mvnw clean install
  if [ $? -ne 0 ]; then
    echo "Le build backend a échoué."
    backend_tests_status="FAILURE"
    # Si le build échoue, on arrête ici car le reste dépend du build
    exit 1
  fi

  # Exécuter les tests backend
  ./mvnw test
  if [ $? -ne 0 ]; then
    echo "Les tests back-end ont échoué."
    backend_tests_status="FAILURE"
  else
    echo "Les tests back-end ont réussi !"
  fi

  # Démarrer l'application backend
  ./mvnw spring-boot:run &
  echo "Backend installé et démarré"
  cd ..
else
  echo "Le répertoire backend n'existe pas."
  exit 1
fi

# Récapitulatif des résultats des tests
echo "======================="
echo "Résumé des tests"
echo "======================="
echo "Tests front-end : $frontend_tests_status"
echo "Tests back-end : $backend_tests_status"

# Si l'un des tests a échoué, retourner un code d'erreur
if [ "$frontend_tests_status" == "FAILURE" ] || [ "$backend_tests_status" == "FAILURE" ]; then
  echo "Un ou plusieurs tests ont échoué."
  exit 1
else
  echo "Tous les tests ont réussi !"
  exit 0
fi
