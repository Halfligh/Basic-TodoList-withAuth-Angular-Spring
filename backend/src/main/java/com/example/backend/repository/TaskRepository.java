package com.example.backend.repository;

import com.example.backend.model.User;
import com.example.backend.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    // Requête JPQL pour récupérer toutes les tâches avec les utilisateurs et leurs
    // rôles
    @Query("SELECT t FROM Task t JOIN FETCH t.owner o JOIN FETCH o.roles")
    List<Task> findAllWithUsersAndRoles();

    // Requête pour trouver les tâches par propriétaire
    List<Task> findByOwner(User owner);
}
