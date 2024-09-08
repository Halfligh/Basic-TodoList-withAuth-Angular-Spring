// AuthController.java
package com.example.backend.controller;

import com.example.backend.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody Map<String, String> loginRequest) {
        // Extraire les données du JSON de la requête
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");

        // Créer l'objet Authentication
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));

        // Générer le token JWT
        String jwt = tokenProvider.generateToken(authentication);

        // Retourner le token en réponse
        return ResponseEntity.ok(jwt);
    }
}
