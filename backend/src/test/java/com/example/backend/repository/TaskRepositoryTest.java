package com.example.backend.repository;

import com.example.backend.model.Task;
import com.example.backend.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
public class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testFindByOwner_Success() {
        // Supprimez tous les utilisateurs et tâches pour éviter les conflits
        taskRepository.deleteAll();
        userRepository.deleteAll();
        userRepository.flush();

        // Given: Création d'un utilisateur et d'une tâche associée
        User user = new User();
        user.setUsername("JohnDoe");
        user.setPassword("password");
        userRepository.save(user);

        Task task = new Task();
        task.setText("Complete unit testing");
        task.setOwner(user);
        taskRepository.save(task);

        // When: Recherche de tâches associées à cet utilisateur
        List<Task> tasks = taskRepository.findByOwner(user);

        // Then: Vérification que la tâche est bien récupérée
        assertThat(tasks).isNotEmpty();
        assertThat(tasks.get(0).getText()).isEqualTo("Complete unit testing");
    }

    @Test
    public void testFindByOwner_NotFound() {
        taskRepository.deleteAll();
        userRepository.deleteAll();
        userRepository.flush();

        // Given: Un utilisateur sans tâche
        User user = new User();
        user.setUsername("JohnDoe");
        user.setPassword("password");
        userRepository.save(user);

        // When: Recherche de tâches pour cet utilisateur
        List<Task> tasks = taskRepository.findByOwner(user);

        // Then: Vérification qu'aucune tâche n'a été trouvée
        assertThat(tasks).isEmpty();
    }
}