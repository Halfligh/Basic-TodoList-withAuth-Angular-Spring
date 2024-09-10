// TaskService.java
package com.example.backend.service;

import com.example.backend.model.Task;
import com.example.backend.model.User;
import com.example.backend.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
