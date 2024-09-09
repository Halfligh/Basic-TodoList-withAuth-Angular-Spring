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
		Role adminRole = createRoleIfNotExists("ROLE_ADMIN");
		Role userRole = createRoleIfNotExists("ROLE_USER");

		// Ajouter les rôles à un ensemble de rôles
		Set<Role> roles = new HashSet<>();
		roles.add(adminRole);
		roles.add(userRole);

		// Créer un utilisateur par défaut si aucun utilisateur n'existe
		createDefaultUserIfNotExists("admin", "admin", roles);
	}

	// Méthode pour créer un rôle s'il n'existe pas
	private Role createRoleIfNotExists(String roleName) {
		return roleRepository.findByName(roleName).orElseGet(() -> {
			Role newRole = new Role();
			newRole.setName(roleName);
			// Utilisation de saveAndFlush pour s'assurer que le rôle est bien persisté
			// immédiatement
			Role savedRole = roleRepository.saveAndFlush(newRole);
			System.out.println("Rôle créé : " + roleName);
			return savedRole;
		});
	}

	// Méthode pour créer un utilisateur par défaut s'il n'existe pas
	private void createDefaultUserIfNotExists(String username, String password, Set<Role> roles) {
		if (userService.findByUsername(username).isEmpty()) {
			User adminUser = new User();
			adminUser.setUsername(username);
			adminUser.setPassword(passwordEncoder.encode(password)); // Encode the password
			adminUser.setRoles(roles);

			userService.saveUser(adminUser); // Utiliser la méthode saveUser
			System.out.println("Utilisateur par défaut créé : " + adminUser.getUsername());
		} else {
			System.out.println("L'utilisateur par défaut existe déjà.");
		}
	}
}
