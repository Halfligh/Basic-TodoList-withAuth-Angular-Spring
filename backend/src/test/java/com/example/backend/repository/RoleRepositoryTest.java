package com.example.backend.repository;

import com.example.backend.model.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
public class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testFindByName_Success() {
        // Suppression des utilisateurs pour dissocier les rôles avant de les supprimer
        userRepository.deleteAll();
        userRepository.flush(); // Assurez-vous que les suppressions sont immédiates

        // Supprimez tous les rôles pour éviter les conflits de test
        roleRepository.deleteAll();
        roleRepository.flush();

        // Given: Création d'un rôle et enregistrement dans le repository
        Role role = new Role();
        role.setName("ADMIN");
        roleRepository.save(role);

        // When: Recherche du rôle par son nom
        Optional<Role> foundRole = roleRepository.findByName("ADMIN");

        // Then: Vérification que le rôle est bien trouvé
        assertThat(foundRole).isPresent();
        assertThat(foundRole.get().getName()).isEqualTo("ADMIN");
    }

    @Test
    public void testFindByName_NotFound() {
        // Suppression des utilisateurs et rôles
        userRepository.deleteAll();
        userRepository.flush();
        roleRepository.deleteAll();
        roleRepository.flush();

        // When: Recherche d'un rôle qui n'existe pas
        Optional<Role> foundRole = roleRepository.findByName("USER");

        // Then: Vérification que le rôle n'est pas trouvé
        assertThat(foundRole).isNotPresent();
    }
}
