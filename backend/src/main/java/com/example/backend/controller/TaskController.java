package com.example.backend.controller;

import com.example.backend.model.Task;
import com.example.backend.model.User;
import com.example.backend.service.TaskService;
import com.example.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserService userService;

    // Déclaration du logger
    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    // Méthode simplifiée pour obtenir l'utilisateur actuellement authentifié
    private User getCurrentUser() {
        // Supposons que l'utilisateur est toujours authentifié pour simplifier
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            String username = ((UserDetails) authentication.getPrincipal()).getUsername();
            return userService.findByUsername(username).orElse(null); // Retourne null si utilisateur non trouvé
        }
        return null;
    }

    // Récupérer toutes les tâches de l'utilisateur connecté
    @GetMapping
    public List<Task> getUserTasks() {
        User user = getCurrentUser();
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur non authentifié");
        }
        return taskService.getTasksByUser(user);
    }

    // Créer une nouvelle tâche
    @PostMapping
    public Task createTask(@RequestBody Task task) {
        // Log de début de la méthode
        logger.info("Début de la création d'une nouvelle tâche");

        User user = getCurrentUser();
        if (user == null) {
            // Log pour indiquer que l'utilisateur n'est pas authentifié
            logger.error("Problème : utilisateur non authentifié lors de la création d'une tâche");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur non authentifié");
        }

        // Log pour indiquer que l'utilisateur est récupéré avec succès
        logger.info("Utilisateur authentifié : " + user.getUsername());

        // Associer la tâche à l'utilisateur
        task.setOwner(user);

        // Spécifier que ce n'est pas une tâche ajouté par l'admin mais bien par l'user
        task.setAddByAdmin(false);

        // Log pour indiquer que la tâche est bien associée à l'utilisateur
        logger.info("Tâche associée à l'utilisateur : " + user.getUsername());

        try {
            // Créer la tâche et ajouter un log si cela réussit
            Task createdTask = taskService.createTask(task);
            logger.info("Tâche créée avec succès : " + createdTask.getId());
            return createdTask;
        } catch (Exception e) {
            // Log en cas d'erreur lors de la création de la tâche
            logger.error("Erreur lors de la création de la tâche", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erreur lors de la création de la tâche");
        }
    }

    // Méthode pour mettre à jour l'état de la tâche (complétée ou non)
    @PutMapping("/{id}/status")
    public Task updateTaskStatus(@PathVariable Long id, @RequestBody boolean completed) {
        logger.info("Mise à jour de l'état de la tâche avec ID : " + id);

        User user = getCurrentUser();
        if (user == null) {
            logger.error("Problème : utilisateur non authentifié lors de la mise à jour d'une tâche");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur non authentifié");
        }

        try {
            Task updatedTask = taskService.updateTaskStatus(id, completed, user);
            logger.info(
                    "Tâche mise à jour avec succès : " + updatedTask.getId() + ", état : " + updatedTask.isCompleted());
            return updatedTask;
        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour de l'état de la tâche", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erreur lors de la mise à jour de la tâche");
        }
    }

    // Supprimer une tâche
    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
    }

    // Nouvelle méthode pour récupérer les tâches de tous les utilisateurs si
    // l'utilisateur connecté est administrateur
    @GetMapping("/all")
    public Map<String, List<Task>> getAllTasksForAdmin() {
        User currentUser = getCurrentUser();

        // Vérifiez si l'utilisateur a le rôle ADMIN
        if (currentUser != null
                && currentUser.getRoles().stream().anyMatch(role -> role.getName().equals("ADMIN"))) {
            try {
                Map<String, List<Task>> tasksGroupedByUser = taskService.getAllTasksGroupedByUser();
                logger.info("Récupération réussie des tâches pour l'administrateur.");
                return tasksGroupedByUser;
            } catch (Exception e) {
                logger.error("Erreur lors de la récupération des tâches pour l'administrateur.", e);
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Erreur lors de la récupération des tâches.");
            }
        } else {
            logger.warn("Accès interdit : l'utilisateur n'a pas le rôle administrateur.");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Accès interdit : Vous devez être administrateur pour accéder à cette ressource.");
        }
    }

    // Créer une nouvelle tâche pour un utilisateur spécifié (administrateurs
    // uniquement)

    @PostMapping("/{username}/create")
    public Task createTaskForUser(@PathVariable String username, @RequestBody Task task) {
        // Récupérer l'utilisateur actuellement authentifié
        User currentUser = getCurrentUser();

        // Vérifiez si l'utilisateur authentifié a le rôle d'administrateur
        if (currentUser == null || currentUser.getRoles().stream().noneMatch(role -> role.getName().equals("ADMIN"))) {
            logger.warn("Accès interdit : Seul un administrateur peut créer des tâches pour d'autres utilisateurs.");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Accès interdit : Vous devez être administrateur pour créer des tâches pour d'autres utilisateurs.");
        }

        // Rechercher l'utilisateur cible par son nom d'utilisateur
        User targetUser = userService.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur non trouvé"));

        // Associer la tâche à l'utilisateur cible
        task.setOwner(targetUser);
        task.setAddByAdmin(true);

        try {
            // Créer la tâche pour l'utilisateur spécifié
            Task createdTask = taskService.createTask(task);
            logger.info("Tâche créée avec succès pour l'utilisateur : " + targetUser.getUsername());
            return createdTask;
        } catch (Exception e) {
            logger.error("Erreur lors de la création de la tâche pour l'utilisateur : " + targetUser.getUsername(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erreur lors de la création de la tâche.");
        }
    }
}
