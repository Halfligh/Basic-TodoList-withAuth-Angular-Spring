package com.example.backend.service;

import com.example.backend.model.Role;
import com.example.backend.model.User;
import com.example.backend.repository.RoleRepository;
import com.example.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setup() {
        // Initialiser les mocks
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateUser_Success() {
        // Données de test
        String username = "testuser";
        String password = "password";
        Set<Role> roles = new HashSet<>();
        roles.add(new Role("ROLE_USER"));

        // Simuler que l'utilisateur n'existe pas déjà
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Simuler l'encodage du mot de passe
        when(passwordEncoder.encode(password)).thenReturn("encodedPassword");

        // Simuler la récupération ou la création des rôles
        Role role = new Role("ROLE_USER");
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(role));

        // Simuler la sauvegarde de l'utilisateur
        User userToSave = new User();
        userToSave.setUsername(username);
        userToSave.setPassword("encodedPassword");
        userToSave.setRoles(roles);

        when(userRepository.save(any(User.class))).thenReturn(userToSave);

        // Appel de la méthode à tester
        User createdUser = userService.createUser(username, password, roles);

        // Vérification des résultats
        assertNotNull(createdUser);
        assertEquals(username, createdUser.getUsername());
        assertEquals("encodedPassword", createdUser.getPassword());
        assertEquals(1, createdUser.getRoles().size());
        assertTrue(createdUser.getRoles().contains(role));

        // Vérifier que les méthodes des mocks ont bien été appelées
        verify(userRepository, times(1)).findByUsername(username);
        verify(passwordEncoder, times(1)).encode(password);
        verify(roleRepository, times(1)).findByName("ROLE_USER");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testCreateUser_AlreadyExists() {
        // Données de test
        String username = "existingUser";
        String password = "password";
        Set<Role> roles = new HashSet<>();
        roles.add(new Role("ROLE_USER"));

        // Simuler que l'utilisateur existe déjà
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(new User()));

        // Appel de la méthode à tester avec une exception attendue
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser(username, password, roles);
        });

        // Vérification du message de l'exception
        assertEquals("L'utilisateur existe déjà !", exception.getMessage());

        // Vérifier que la méthode save n'a jamais été appelée
        verify(userRepository, never()).save(any(User.class));
    }
}
