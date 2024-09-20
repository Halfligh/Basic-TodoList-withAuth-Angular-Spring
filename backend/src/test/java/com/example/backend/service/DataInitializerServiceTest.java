package com.example.backend.service;

import com.example.backend.model.Role;
import com.example.backend.model.User;
import com.example.backend.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class DataInitializerServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private DataInitializerService dataInitializerService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testInitializeData_CreatesRolesAndUsers() {
        // Mock the roleRepository to simulate no roles exist
        when(roleRepository.findByName("ADMIN")).thenReturn(Optional.empty());
        when(roleRepository.findByName("USER")).thenReturn(Optional.empty());

        // Mock the userService to simulate no users exist
        when(userService.findByUsername(anyString())).thenReturn(Optional.empty());

        // Mock the save methods
        when(roleRepository.saveAndFlush(any(Role.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userService.saveUser(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Run the initialization
        dataInitializerService.initializeData();

        // Verify that roles were created (2 times: once for ADMIN, once for USER)
        verify(roleRepository, times(2)).saveAndFlush(any(Role.class)); // <- Modifier cette ligne
        verify(userService, times(3)).saveUser(any(User.class)); // 3 users: admin, JohnDoe, MelanieDoe
    }

    @Test
    public void testInitializeData_DoesNotDuplicateRolesAndUsers() {
        // Mock the roleRepository to simulate roles already exist
        Role adminRole = new Role();
        adminRole.setName("ADMIN");
        Role userRole = new Role();
        userRole.setName("USER");

        when(roleRepository.findByName("ADMIN")).thenReturn(Optional.of(adminRole));
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(userRole));

        // Mock the userService to simulate users already exist
        when(userService.findByUsername("admin")).thenReturn(Optional.of(new User()));
        when(userService.findByUsername("JohnDoe")).thenReturn(Optional.of(new User()));
        when(userService.findByUsername("MelanieDoe")).thenReturn(Optional.of(new User()));

        // Run the initialization
        dataInitializerService.initializeData();

        // Verify that no new roles or users were created
        verify(roleRepository, never()).saveAndFlush(any(Role.class));
        verify(userService, never()).saveUser(any(User.class));
    }
}
