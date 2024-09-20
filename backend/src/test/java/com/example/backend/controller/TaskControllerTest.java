package com.example.backend.controller;

import com.example.backend.model.Task;
import com.example.backend.model.User;
import com.example.backend.model.Role;
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
import java.util.Map;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @MockBean
    private UserService userService;

    private User mockUser;
    private User mockAdmin; // Déclarer la variable pour l'administrateur

    @BeforeEach
    public void setUp() {
        // Création de l'utilisateur normal mocké
        mockUser = new User();
        mockUser.setUsername("testuser");
        mockUser.setId(1L);

        // Création de l'utilisateur admin mocké
        mockAdmin = new User(); // Correction de la variable ici
        mockAdmin.setUsername("admin");
        mockAdmin.setId(2L);

        // Création du rôle ADMIN et association à l'utilisateur admin
        Role adminRole = new Role();
        adminRole.setName("ADMIN");

        Set<Role> roles = new HashSet<>(); // Correction du type Set
        roles.add(adminRole);
        mockAdmin.setRoles(roles); // Correction de l'appel à setRoles
    }

    // Test pour récupérer les tâches de l'utilisateur authentifié
    @Test
    @WithMockUser(username = "testuser", roles = { "USER" })
    public void testGetUserTasks_Success() throws Exception {
        List<Task> tasks = List.of(new Task("Task 1", false, mockUser, false));
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
        Task newTask = new Task("New Task", false, mockUser, false);
        newTask.setId(2L);

        when(userService.findByUsername("testuser")).thenReturn(Optional.of(mockUser));
        when(taskService.createTask(Mockito.any(Task.class))).thenReturn(newTask);

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"text\": \"New Task\" }"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.text").value("New Task"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = { "USER" })
    public void testUpdateTaskStatus_Success() throws Exception {
        Task updatedTask = new Task("Task 1", true, mockUser, false);
        when(userService.findByUsername("testuser")).thenReturn(Optional.of(mockUser));
        when(taskService.updateTaskStatus(1L, true, mockUser)).thenReturn(updatedTask);

        mockMvc.perform(put("/api/tasks/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Task 1"))
                .andExpect(jsonPath("$.completed").value(true));
    }

    @Test
    @WithMockUser(username = "testuser", roles = { "USER" })
    public void testDeleteTask_Success() throws Exception {
        doNothing().when(taskService).deleteTask(1L);

        mockMvc.perform(delete("/api/tasks/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", roles = { "ADMIN" })
    public void testGetAllTasksForAdmin_Success() throws Exception {
        when(userService.findByUsername("admin")).thenReturn(Optional.of(mockAdmin));
        Map<String, List<Task>> tasks = Map.of("user1", List.of(new Task("Task 1", false, mockUser, false)));
        when(taskService.getAllTasksGroupedByUser()).thenReturn(tasks);

        mockMvc.perform(get("/api/tasks/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user1[0].text").value("Task 1"));
    }

    @Test
    @WithMockUser(username = "admin", roles = { "ADMIN" })
    public void testCreateTaskForUser_Success() throws Exception {
        Task newTask = new Task("Admin Created Task", false, mockUser, true);
        when(userService.findByUsername("admin")).thenReturn(Optional.of(mockAdmin));
        when(userService.findByUsername("user1")).thenReturn(Optional.of(mockUser));
        when(taskService.createTask(Mockito.any(Task.class))).thenReturn(newTask);

        mockMvc.perform(post("/api/tasks/user1/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"text\": \"Admin Created Task\" }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Admin Created Task"));
    }
}
