package com.example.backend.service;

import com.example.backend.model.Task;
import com.example.backend.model.User;
import com.example.backend.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetTasksByUser() {
        // Préparer les données de test
        User user = new User();
        user.setId(1L);
        Task task1 = new Task();
        task1.setId(1L);
        task1.setOwner(user);
        Task task2 = new Task();
        task2.setId(2L);
        task2.setOwner(user);

        List<Task> mockTasks = Arrays.asList(task1, task2);

        // Simuler le comportement de la méthode mockée
        when(taskRepository.findByOwner(user)).thenReturn(mockTasks);

        // Appeler la méthode à tester
        List<Task> tasks = taskService.getTasksByUser(user);

        // Vérifier les résultats
        assertNotNull(tasks);
        assertEquals(2, tasks.size());
        assertEquals(1L, tasks.get(0).getId());
        assertEquals(2L, tasks.get(1).getId());

        // Vérifier que la méthode `findByOwner` a été appelée une fois
        verify(taskRepository, times(1)).findByOwner(user);
    }

    @Test
    public void testCreateTask() {
        // Préparer les données de test
        Task task = new Task();
        task.setId(1L);

        // Simuler le comportement de la méthode `save`
        when(taskRepository.save(task)).thenReturn(task);

        // Appeler la méthode à tester
        Task createdTask = taskService.createTask(task);

        // Vérifier les résultats
        assertNotNull(createdTask);
        assertEquals(1L, createdTask.getId());

        // Vérifier que la méthode `save` a été appelée une fois
        verify(taskRepository, times(1)).save(task);
    }

    @Test
    public void testUpdateTaskStatus_Success() {
        // Préparer les données de test
        User user = new User();
        user.setId(1L);
        Task task = new Task();
        task.setCompleted(false); // La tâche n'est pas encore complétée
        task.setId(1L);
        task.setOwner(user);

        // Simuler la méthode `findById`
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        // Appeler la méthode à tester
        Task updatedTask = taskService.updateTaskStatus(1L, true, user);

        // Vérifier que la tâche a été mise à jour
        assertNotNull(updatedTask);
        assertTrue(updatedTask.isCompleted());

        // Vérifier que la méthode `save` a été appelée
        verify(taskRepository, times(1)).findById(1L);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    public void testUpdateTaskStatus_Unauthorized() {
        // Préparer les données de test
        User user1 = new User();
        user1.setId(1L);
        User user2 = new User();
        user2.setId(2L);
        Task task = new Task();
        task.setId(1L);
        task.setOwner(user2); // La tâche appartient à un autre utilisateur

        // Simuler la méthode `findById`
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        // Vérifier que l'exception est levée lorsque l'utilisateur n'est pas le
        // propriétaire
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            taskService.updateTaskStatus(1L, true, user1);
        });

        // Vérifier que l'exception a le bon message
        assertEquals("Vous n'êtes pas autorisé à modifier cette tâche", exception.getMessage());

        // Vérifier que la méthode `save` n'a jamais été appelée
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    public void testDeleteTask() {
        // Appeler la méthode à tester
        taskService.deleteTask(1L);

        // Vérifier que la méthode `deleteById` a été appelée
        verify(taskRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testGetAllTasksGroupedByUser() {
        // Préparer les données de test
        User user1 = new User();
        user1.setUsername("user1");
        User user2 = new User();
        user2.setUsername("user2");

        Task task1 = new Task();
        task1.setOwner(user1);
        Task task2 = new Task();
        task2.setOwner(user2);

        List<Task> mockTasks = Arrays.asList(task1, task2);

        // Simuler le comportement de la méthode mockée
        when(taskRepository.findAllWithUsersAndRoles()).thenReturn(mockTasks);

        // Appeler la méthode à tester
        Map<String, List<Task>> groupedTasks = taskService.getAllTasksGroupedByUser();

        // Vérifier les résultats
        assertNotNull(groupedTasks);
        assertEquals(2, groupedTasks.size());
        assertTrue(groupedTasks.containsKey("user1"));
        assertTrue(groupedTasks.containsKey("user2"));

        // Vérifier que la méthode `findAllWithUsersAndRoles` a été appelée une fois
        verify(taskRepository, times(1)).findAllWithUsersAndRoles();
    }
}
