package com.example.backend.service;

import com.example.backend.model.Role;
import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;
import com.example.backend.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Méthode pour créer un nouvel utilisateur avec des rôles
    @Transactional
    public User createUser(String username, String password, Set<Role> roles) {
        // Vérifier si l'utilisateur existe déjà
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("L'utilisateur existe déjà !");
        }

        // Attacher les rôles au contexte de persistance
        Set<Role> attachedRoles = new HashSet<>();
        for (Role role : roles) {
            // Récupérer le rôle depuis la base de données ou le sauvegarder
            Role existingRole = roleRepository.findByName(role.getName())
                    .orElseGet(() -> {
                        Role newRole = new Role(role.getName());
                        return roleRepository.saveAndFlush(newRole); // Utilisation de saveAndFlush
                    });
            attachedRoles.add(existingRole);
        }

        // Créer un nouvel utilisateur avec les rôles attachés
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(attachedRoles);

        // Sauvegarder l'utilisateur avec ses rôles
        return userRepository.save(user);
    }

    // Méthode pour sauvegarder un utilisateur existant ou mis à jour
    public User saveUser(User user) {
        // Assurez-vous que le mot de passe est encodé si nécessaire
        if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }

    // Méthode pour trouver un utilisateur par son nom d'utilisateur
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // Autres méthodes pour gérer les utilisateurs selon vos besoins
}
