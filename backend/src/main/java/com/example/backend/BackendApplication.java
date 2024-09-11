package com.example.backend;

import com.example.backend.model.Role;
import com.example.backend.model.User;
import com.example.backend.repository.RoleRepository;
import com.example.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
public class BackendApplication implements CommandLineRunner {

	@Autowired
	private UserService userService;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

	// Méthode exécutée au démarrage de l'application
	@Override
	@Transactional // Ajout de @Transactional pour gérer le contexte de persistance
	public void run(String... args) {
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
	}

	// Méthode pour créer un rôle s'il n'existe pas
	private Role createRoleIfNotExists(String roleName) {
		return roleRepository.findByName(roleName).orElseGet(() -> {
			Role newRole = new Role();
			newRole.setName(roleName);
			Role savedRole = roleRepository.saveAndFlush(newRole);
			System.out.println("Rôle créé : " + roleName);
			return savedRole;
		});
	}

	// Méthode pour créer un utilisateur par défaut s'il n'existe pas
	private void createDefaultUserIfNotExists(String username, String password, Set<Role> roles) {
		if (userService.findByUsername(username).isEmpty()) {
			User user = new User();
			user.setUsername(username);
			user.setPassword(passwordEncoder.encode(password)); // Encoder le mot de passe
			user.setRoles(roles);

			userService.saveUser(user); // Sauvegarder l'utilisateur
			System.out.println("Utilisateur créé : " + user.getUsername());
		} else {
			System.out.println("L'utilisateur " + username + " existe déjà.");
		}
	}
}
