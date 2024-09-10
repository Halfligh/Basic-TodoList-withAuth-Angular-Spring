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

    // Supprimer une tâche
    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
    }
}
