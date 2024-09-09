// TaskRepository.java
package com.example.backend.repository;

import com.example.backend.model.Task;
import com.example.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByOwner(User owner); // Requête pour trouver les tâches par propriétaire
}
