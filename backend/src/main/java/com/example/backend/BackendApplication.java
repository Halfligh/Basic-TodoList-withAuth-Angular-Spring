package com.example.backend;

import com.example.backend.model.Role;
import com.example.backend.model.User;
import com.example.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
public class BackendApplication implements CommandLineRunner {

	@Autowired
	private UserService userService;

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

	// Méthode exécutée au démarrage de l'application
	@Override
	public void run(String... args) {
		// Créer un rôle par défaut (ex : ROLE_ADMIN)
		Role adminRole = new Role();
		adminRole.setName("ROLE_ADMIN");

		// Ajouter le rôle à un ensemble de rôles
		Set<Role> roles = new HashSet<>();
		roles.add(adminRole);

		// Créer un utilisateur par défaut si aucun utilisateur n'existe
		if (userService.findByUsername("admin").isEmpty()) {
			User adminUser = userService.createUser("admin", "admin", roles);
			System.out.println("Utilisateur par défaut créé : " + adminUser.getUsername());
		} else {
			System.out.println("L'utilisateur par défaut existe déjà.");
		}
	}
}
