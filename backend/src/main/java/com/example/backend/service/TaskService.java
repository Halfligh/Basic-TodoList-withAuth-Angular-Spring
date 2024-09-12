package com.example.backend.service;

import com.example.backend.model.Task;
import com.example.backend.model.User;
import com.example.backend.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    // Récupérer toutes les tâches d'un utilisateur
    public List<Task> getTasksByUser(User user) {
        return taskRepository.findByOwner(user);
    }

    // Créer une tâche
    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    // Méthode pour mettre à jour l'état d'une tâche
    public Task updateTaskStatus(Long id, boolean completed, User user) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tâche non trouvée avec l'ID : " + id));

        // Vérifie que la tâche appartient à l'utilisateur actuel
        if (!task.getOwner().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Vous n'êtes pas autorisé à modifier cette tâche");
        }

        task.setCompleted(completed);
        return taskRepository.save(task);
    }

    // Supprimer une tâche
    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    // Méthode pour récupérer les tâches de tous les utilisateurs et les regrouper
    // par nom d'utilisateur
    // Méthode pour récupérer les tâches de tous les utilisateurs et les regrouper
    // par nom d'utilisateur
    @Transactional
    public Map<String, List<Task>> getAllTasksGroupedByUser() {
        // Récupère toutes les tâches avec les utilisateurs et leurs rôles
        List<Task> tasks = taskRepository.findAllWithUsersAndRoles();

        // Groupage des tâches par le nom d'utilisateur
        return tasks.stream()
                .collect(Collectors.groupingBy(task -> task.getOwner().getUsername()));
    }
}
