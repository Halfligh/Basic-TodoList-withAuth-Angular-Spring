package com.example.backend;

import com.example.backend.service.DataInitializerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackendApplication implements CommandLineRunner {

	private final DataInitializerService dataInitializerService;

	@Autowired
	public BackendApplication(DataInitializerService dataInitializerService) {
		this.dataInitializerService = dataInitializerService;
	}

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

	@Override
	public void run(String... args) {
		dataInitializerService.initializeData(); // Appeler le service d'initialisation des données
	}
}
