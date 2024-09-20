package com.example.backend.controller;

import com.example.backend.model.Task;
import com.example.backend.model.User;
import com.example.backend.service.TaskService;
import com.example.backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // <- Remplacez @WebMvcTest ici
@AutoConfigureMockMvc // <- Ajoutez ceci pour permettre l'utilisation de MockMvc avec SpringBootTest
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @MockBean
    private UserService userService;

    private User mockUser;

    @BeforeEach
    public void setUp() {
        mockUser = new User();
        mockUser.setUsername("testuser");
        mockUser.setId(1L);
    }

    // Test pour récupérer les tâches de l'utilisateur authentifié
    @Test
    @WithMockUser(username = "testuser", roles = { "USER" })
    public void testGetUserTasks_Success() throws Exception {
        List<Task> tasks = List.of(new Task("Task 1", false, mockUser, false)); // Utilisation du constructeur
        when(userService.findByUsername("testuser")).thenReturn(Optional.of(mockUser));
        when(taskService.getTasksByUser(mockUser)).thenReturn(tasks);

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].text").value("Task 1"));
    }

    // Test pour créer une tâche avec un utilisateur authentifié
    @Test
    @WithMockUser(username = "testuser", roles = { "USER" })
    public void testCreateTask_Success() throws Exception {
        Task newTask = new Task("New Task", false, mockUser, false); // Utiliser le constructeur valide
        newTask.setId(2L); // On peut définir l'ID manuellement pour le test

        when(userService.findByUsername("testuser")).thenReturn(Optional.of(mockUser));
        when(taskService.createTask(Mockito.any(Task.class))).thenReturn(newTask);

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"text\": \"New Task\" }")) // Remplacer "name" par "text"
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.text").value("New Task"));
    }
}
