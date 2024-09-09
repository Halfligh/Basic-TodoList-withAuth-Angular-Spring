// Task.java
package com.example.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String text;

    private boolean completed;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false) // Clé étrangère pour lier à l'utilisateur
    private User owner; // Relation avec l'utilisateur propriétaire

    // Constructeurs, getters et setters
    public Task() {
    }

    public Task(String text, boolean completed, User owner) {
        this.text = text;
        this.completed = completed;
        this.owner = owner;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }
}
