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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testLoadUserByUsername_UserExists() {
        // Préparer les données de test
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setRoles(Set.of(new Role("USER")));

        // Simuler le comportement du repository
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        // Appeler la méthode à tester
        UserDetails userDetails = userService.loadUserByUsername("testuser");

        // Vérifier le résultat
        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        assertEquals("password", userDetails.getPassword());

        // Vérifier que l'utilisateur a le rôle attendu
        assertTrue(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    public void testLoadUserByUsername_UserNotFound() {
        // Simuler que l'utilisateur n'existe pas
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Vérifier que l'exception `UsernameNotFoundException` est bien levée
        assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername("nonexistent");
        });

        // Vérifier que la méthode `findByUsername` a été appelée une fois
        verify(userRepository, times(1)).findByUsername("nonexistent");
    }

    @Test
    public void testSaveUser_EncodesPassword() {
        // Préparer les données de test
        User mockUser = new User();
        mockUser.setUsername("testuser");
        mockUser.setPassword("plaintextPassword");

        // Simuler le comportement de `passwordEncoder`
        when(passwordEncoder.encode("plaintextPassword")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        // Appeler la méthode à tester
        User savedUser = userService.saveUser(mockUser);

        // Vérifier que le mot de passe a été encodé
        assertEquals("encodedPassword", savedUser.getPassword());

        // Vérifier que `passwordEncoder.encode` et `userRepository.save` ont été
        // appelés
        verify(passwordEncoder, times(1)).encode("plaintextPassword");
        verify(userRepository, times(1)).save(mockUser);
    }

    @Test
    public void testSaveUser_AlreadyEncodedPassword() {
        // Préparer un utilisateur dont le mot de passe est déjà encodé
        User mockUser = new User();
        mockUser.setUsername("testuser");
        mockUser.setPassword("$2a$10$encodedPassword");

        // Simuler la sauvegarde de l'utilisateur
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        // Appeler la méthode à tester
        User savedUser = userService.saveUser(mockUser);

        // Vérifier que le mot de passe n'a pas été ré-encodé
        assertEquals("$2a$10$encodedPassword", savedUser.getPassword());

        // Vérifier que `passwordEncoder.encode` n'a pas été appelé
        verify(passwordEncoder, times(0)).encode(anyString());

        // Vérifier que `userRepository.save` a été appelé
        verify(userRepository, times(1)).save(mockUser);
    }
}
