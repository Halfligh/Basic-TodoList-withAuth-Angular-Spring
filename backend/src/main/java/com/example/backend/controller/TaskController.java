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

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserService userService;

    // Méthode privée pour obtenir l'utilisateur actuellement authentifié
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            return userService.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur non trouvé"));
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur non authentifié");
    }

    // Récupérer toutes les tâches de l'utilisateur connecté
    @GetMapping
    public List<Task> getUserTasks() {
        User user = getCurrentUser();
        return taskService.getTasksByUser(user);
    }

    // Créer une nouvelle tâche
    @PostMapping
    public Task createTask(@RequestBody Task task) {
        User user = getCurrentUser();
        task.setOwner(user); // Associe la tâche à l'utilisateur connecté
        return taskService.createTask(task);
    }

    // Supprimer une tâche
    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
    }
}
