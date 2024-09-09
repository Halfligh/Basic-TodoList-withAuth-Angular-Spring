package com.example.backend;

import com.example.backend.model.Role;
import com.example.backend.model.User;
import com.example.backend.repository.RoleRepository;
import com.example.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@SpringBootApplication
public class BackendApplication implements CommandLineRunner {

	@Autowired
	private UserService userService;

	@Autowired
	private RoleRepository roleRepository;

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

	// Méthode exécutée au démarrage de l'application
	@Override
	public void run(String... args) {
		// Créer le rôle par défaut si nécessaire
		Role adminRole = createRoleIfNotExists("ROLE_ADMIN");

		// Ajouter le rôle à un ensemble de rôles
		Set<Role> roles = new HashSet<>();
		roles.add(adminRole);

		// Créer un utilisateur par défaut si aucun utilisateur n'existe
		createDefaultUserIfNotExists("admin", "admin", roles);
	}

	// Méthode pour créer un rôle s'il n'existe pas
	private Role createRoleIfNotExists(String roleName) {
		return roleRepository.findByName(roleName).orElseGet(() -> {
			Role newRole = new Role();
			newRole.setName(roleName);
			Role savedRole = roleRepository.save(newRole);
			System.out.println("Rôle par défaut créé : " + roleName);
			return savedRole;
		});
	}

	// Méthode pour créer un utilisateur par défaut s'il n'existe pas
	private void createDefaultUserIfNotExists(String username, String password, Set<Role> roles) {
		if (userService.findByUsername(username).isEmpty()) {
			User adminUser = userService.createUser(username, password, roles);
			System.out.println("Utilisateur par défaut créé : " + adminUser.getUsername());
		} else {
			System.out.println("L'utilisateur par défaut existe déjà.");
		}
	}
}
