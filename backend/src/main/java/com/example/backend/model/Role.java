package com.example.backend.model;

// Importations nécessaires pour JPA
import jakarta.persistence.*;

@Entity // Indique que cette classe est une entité JPA
@Table(name = "roles") // Spécifie le nom de la table dans la base de données
public class Role {

    @Id // Indique que ce champ est la clé primaire
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incrémentation pour la clé primaire
    private Long id;

    private String name; // Nom du rôle (admin, user, etc.)

    // Constructeurs, getters et setters
    public Role() {
    }

    public Role(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
