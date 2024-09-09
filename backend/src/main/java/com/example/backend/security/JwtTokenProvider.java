// JwtTokenProvider.java
package com.example.backend.security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;

@Component // Assurez-vous que cette annotation est présente
public class JwtTokenProvider {

    private final Key key = Keys.hmacShaKeyFor("votre_cle_secrete_32_caracteres_minimum".getBytes());

    public String generateToken(Authentication authentication) {
        return Jwts.builder()
                .setSubject(authentication.getName())
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}
