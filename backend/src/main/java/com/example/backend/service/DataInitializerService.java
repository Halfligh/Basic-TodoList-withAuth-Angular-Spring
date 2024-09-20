package com.example.backend.service;

import com.example.backend.model.Role;
import com.example.backend.model.User;
import com.example.backend.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
public class DataInitializerService {

    private final UserService userService;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataInitializerService(UserService userService, RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void initializeData() {
        // Créer les rôles par défaut si nécessaire
        Role adminRole = createRoleIfNotExists("ADMIN");
        Role userRole = createRoleIfNotExists("USER");

        // Créer l'utilisateur admin avec les rôles ROLE_ADMIN et ROLE_USER
        Set<Role> adminRoles = new HashSet<>();
        adminRoles.add(adminRole);
        adminRoles.add(userRole);
        createDefaultUserIfNotExists("admin", "admin", adminRoles);

        // Créer l'utilisateur JohnDoe avec le rôle ROLE_USER
        Set<Role> userRoles = new HashSet<>();
        userRoles.add(userRole);
        createDefaultUserIfNotExists("JohnDoe", "Doe", userRoles);

        // Créer l'utilisateur MelanieDoe avec le rôle ROLE_USER
        createDefaultUserIfNotExists("MelanieDoe", "Doe", userRoles);
    }

    private Role createRoleIfNotExists(String roleName) {
        return roleRepository.findByName(roleName).orElseGet(() -> {
            Role newRole = new Role();
            newRole.setName(roleName);
            return roleRepository.saveAndFlush(newRole);
        });
    }

    private void createDefaultUserIfNotExists(String username, String password, Set<Role> roles) {
        if (userService.findByUsername(username).isEmpty()) {
            User user = new User();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(password));
            user.setRoles(roles);
            userService.saveUser(user);
        }
    }
}
