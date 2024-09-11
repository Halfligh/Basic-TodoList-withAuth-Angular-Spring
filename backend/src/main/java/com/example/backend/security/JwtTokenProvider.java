// JwtTokenProvider.java
package com.example.backend.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    // Clé secrète de 32 caractères minimum
    private final Key key = Keys.hmacShaKeyFor("votre_cle_secrete_32_caracteres_minimum".getBytes());

    // Durée de validité du token en millisecondes (par exemple, 1 heure)
    private static final long EXPIRATION_TIME = 3600000;

    public Key getKey() {
        return key; // Retourne la clé définie dans JwtTokenProvider
    }

    // Méthode pour générer le token JWT
    public String generateToken(Authentication authentication) {
        // Date actuelle et date d'expiration
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME);

        // Extraction des rôles de l'utilisateur
        String roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        // Construction du token avec les claims nécessaires
        return Jwts.builder()
                .setSubject(authentication.getName()) // Sujet du token : nom de l'utilisateur
                .claim("roles", roles) // Ajout des rôles dans le token
                .setIssuedAt(now) // Date d'émission
                .setExpiration(expiryDate) // Date d'expiration
                .signWith(key, SignatureAlgorithm.HS256) // Signature avec la clé secrète et algorithme HS256
                .compact();
    }
}
