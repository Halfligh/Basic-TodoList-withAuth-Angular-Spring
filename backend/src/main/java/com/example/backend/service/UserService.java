package com.example.backend.service;

import com.example.backend.model.Role;
import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Méthode pour créer un nouvel utilisateur avec des rôles
    public User createUser(String username, String password, Set<Role> roles) {
        // Vérifier si l'utilisateur existe déjà
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("L'utilisateur existe déjà !");
        }

        // Créer un nouvel utilisateur
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(roles != null ? roles : new HashSet<>()); // Ajout des rôles par défaut s'il n'y en a pas

        // Sauvegarder l'utilisateur avec ses rôles
        return userRepository.save(user);
    }

    // Méthode pour trouver un utilisateur par son nom d'utilisateur
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // Autres méthodes pour gérer les utilisateurs selon vos besoins
}
