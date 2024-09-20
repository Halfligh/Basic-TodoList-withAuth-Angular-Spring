package com.example.backend.repository;

import com.example.backend.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testFindByUsername_Success() {
        userRepository.deleteAll();
        userRepository.flush();

        // Given: Création d'un utilisateur et enregistrement dans le repository
        User user = new User();
        user.setUsername("JohnDoe");
        user.setPassword("password");
        userRepository.save(user);

        // When: Recherche par nom d'utilisateur
        Optional<User> foundUser = userRepository.findByUsername("JohnDoe");

        // Then: Vérification que l'utilisateur est trouvé
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("JohnDoe");
    }

    @Test
    public void testFindByUsername_NotFound() {
        // When: Recherche d'un utilisateur qui n'existe pas
        Optional<User> foundUser = userRepository.findByUsername("JaneDoe");

        // Then: Vérification que l'utilisateur n'est pas trouvé
        assertThat(foundUser).isNotPresent();
    }
}
