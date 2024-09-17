# Angular
Projet Angular to learn 
Fonctionnalités Clés de l’Application


Authentification Utilisateur
L'application inclut un système de connexion et de déconnexion géré par des services Angular.

Gestion de todo-lists avec CRUD (Create, Read, Update, Delete) : Via une Api Java SpringBoot et une base de donnée MySQL
Les utilisateurs peuvent créer, consulter, modifier et supprimer des tâches via une interface intuitive. 

Configuration des rôles pour les utilisateurs et privilèges administrateur
Les utilisateurs ayant le rôle administrateur peuvent accéder aux tâches de tous les utilisateurs et leurs ajouter des tâches, l'application mentionne à l'utilisateur que c'est l'administrateur qui lui a ajouté cette tâche. 

Historique du développement de ce projet 

Création du Frontend avec Angular en initialisant un nouveau projet 
- Création des composants
- Création des services
- Création des styles


Création de la Base De Données Manuellement avec MySQL 
- Installation de MySQL 8.4
- Lancer MySQL : mysql -u root -p (vérifier que le brew service est lancé)
- CREATE DATABASE angular_to_do_list


Création du Backend avec Spring Initializr
- Initialisation d'un nouvea projet via Spring Initializr
<img width="1679" alt="Capture d’écran 2024-09-03 à 00 04 43" src="https://github.com/user-attachments/assets/f69daf09-9ea0-4495-b55d-6b0d5c0899b2">
Configuration du fichier application.properties dans ressources
- backend : ./mvnw spring-boot:run
- Using generated security password: b0fd7b3f-e7c2-4db8-8aa6-d1aa9f96d699 (l'un des carctères est différent)
- Création des models User et Role avec Spring 
- Correction du bug d'import javax en remplaçant la dépendance javax par la dépendence jakarta 
- Correction des imports : import jakarta.persistence.*;
- Connexion de l'api Spring à la base de donnée MySQL 
- Configuration d'un Command Line Runner qui permet d'automatiquement créer un utilisateur admin
- Mise en place de l'authentification utilisant un générateur et un validateur de JWTtoken
- Création du model Task et mise en place de la logique de CRUD en associant chaque tâche à son user 
- Ajout dans le Command Line Runner de la création d'un deuxième utilisateur JohnDoe, et MelanieDoe passe : Doe avec le rôle user uniquement
- Ajout de la logique back permettant de créer des tâches en tant qu'admin pour les users 
- Ajout de la logique front pour permettre de reconnaîtres que les tâches ont été ajouté par l'admin

Création du plan de tests automatisés 
- Installation de Jenkins avec Homebrew en local pour ce projet et les futurs projets
- Configuration de base et premier build via Jenkins exécuté 
- Ajout d'un dossier scripts avec le script shell pour Jenkins et ajout de la permission via chmod +x
- Rédaction de l'ensemble des tests unitaires pour le front-end (Jasmine) 
- Création d'un template pour vue globale des tests front
![Tests-Angular-Sébastien](https://github.com/user-attachments/assets/dd315a24-c0cd-4cfe-bd94-1913bca54565)

  
  
