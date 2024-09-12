package com.example.backend.config;

import com.example.backend.service.UserDetailsServiceImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Key;
import java.util.Date;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);
    private final UserDetailsServiceImpl userDetailsService;

    // Clé secrète pour signer et vérifier les JWT (doit être de 32 caractères
    // minimum)
    private final Key key = Keys.hmacShaKeyFor("votre_cle_secrete_32_caracteres_minimum".getBytes());

    @Autowired
    public SecurityConfig(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        logger.info("Début de la configuration du SecurityFilterChain");

        // Configuration de la sécurité HTTP
        http.csrf(csrf -> {
            csrf.disable();
            logger.info("CSRF désactivé");
        })
                .cors(cors -> {
                    cors.configurationSource(corsConfigurationSource());
                    logger.info("Configuration CORS ajoutée");
                })
                .authorizeHttpRequests(authorize -> {
                    authorize
                            .requestMatchers("/api/auth/**").permitAll()
                            .requestMatchers("/api/tasks/all").permitAll()
                            .requestMatchers("/api/tasks/**").authenticated()
                            .anyRequest().authenticated();
                    logger.info("Configuration des autorisations HTTP définie");
                })
                .httpBasic(httpBasic -> {
                    httpBasic.disable();
                    logger.info("Authentification HTTP basique désactivée");
                })
                // Ajouter le filtre JWT avant UsernamePasswordAuthenticationFilter
                .addFilterBefore(new OncePerRequestFilter() {
                    @Override
                    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                            FilterChain filterChain)
                            throws ServletException, IOException {

                        String header = request.getHeader("Authorization");
                        String token = null;
                        String username = null;

                        if (header != null && header.startsWith("Bearer ")) {
                            token = header.substring(7); // Enlève le préfixe "Bearer "
                            logger.info("Token reçu : " + token);

                            try {
                                Claims claims = Jwts.parserBuilder()
                                        .setSigningKey(key)
                                        .build()
                                        .parseClaimsJws(token)
                                        .getBody();
                                username = claims.getSubject();
                                logger.info("Utilisateur extrait du token : " + username);

                                // Vérification si le token est expiré
                                if (claims.getExpiration().before(new Date())) {
                                    logger.error("Le token est expiré");
                                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                    return;
                                }

                            } catch (Exception e) {
                                logger.error("Erreur lors de la validation du token JWT : ", e);
                                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                return;
                            }
                        } else {
                            logger.warn("Aucun token reçu ou format incorrect");
                        }

                        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                            logger.info("Rôles de l'utilisateur : " + userDetails.getAuthorities());
                            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());
                            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                            logger.info("Authentification réussie pour l'utilisateur : " + username);
                        }

                        filterChain.doFilter(request, response);
                    }
                }, UsernamePasswordAuthenticationFilter.class);

        logger.info("Fin de la configuration du SecurityFilterChain");

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        logger.info("Création du AuthenticationManager");
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        logger.info("Création du PasswordEncoder BCrypt");
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        logger.info("Début de la configuration CORS");

        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        logger.info("Configuration CORS définie : " + configuration.toString());

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        logger.info("Configuration CORS enregistrée pour toutes les URLs");

        return source;
    }
}
