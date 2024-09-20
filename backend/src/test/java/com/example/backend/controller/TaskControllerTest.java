package com.example.backend.controller;

import com.example.backend.model.Task;
import com.example.backend.model.User;
import com.example.backend.model.Role;
import com.example.backend.service.TaskService;
import com.example.backend.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;
import java.util.HashSet;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @MockBean
    private UserService userService;

    private User testUser;
    private Task testTask;

    @BeforeEach
    void setUp() {
        // Utilisateur avec le rôle USER
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testUser");
        testUser.setRoles(new HashSet<>(Collections.singletonList(new Role("ROLE_USER"))));

        // Tâche associée à l'utilisateur testUser
        testTask = new Task();
        testTask.setId(1L);
        testTask.setText("Test Task");
        testTask.setOwner(testUser);
    }

    private void mockAuthenticationAsUser() {
        // Créer un UserDetails basé sur testUser avec le rôle ROLE_USER
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                testUser.getUsername(),
                "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));

        // Mock Authentication et SecurityContext
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(userService.findByUsername(testUser.getUsername())).thenReturn(Optional.of(testUser));
    }

    @Test
    void getUserTasks_ShouldReturnUserTasks_WhenUserIsAuthenticated() throws Exception {
        // Mock de l'authentification
        mockAuthenticationAsUser();

        // Simulation du comportement du service
        when(taskService.getTasksByUser(any(User.class)))
                .thenReturn(Collections.singletonList(testTask));

        // Simuler une requête GET sans token JWT, juste avec SecurityContextHolder
        mockMvc.perform(get("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].text").value("Test Task"));

        // Vérification que la méthode du service a été appelée
        verify(taskService, times(1)).getTasksByUser(any(User.class));
    }
}
