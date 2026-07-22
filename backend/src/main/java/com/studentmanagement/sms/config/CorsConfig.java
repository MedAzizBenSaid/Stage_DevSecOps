package com.studentmanagement.sms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration CORS (Cross-Origin Resource Sharing).
 *
 * Par défaut, un navigateur bloque les requêtes faites depuis un domaine
 * différent de celui du serveur (ex: Angular sur le port 4200 qui appelle
 * Spring Boot sur le port 8081).
 *
 * Cette classe autorise explicitement le frontend Angular à communiquer
 * avec notre API REST, sans quoi toutes les requêtes HTTP échoueraient
 * avec une erreur "blocked by CORS policy".
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")                    // Toutes les routes commençant par /api
                .allowedOrigins("http://localhost:4200")   // Origine autorisée : Angular en dev
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")                       // Autorise tous les headers (Content-Type, Authorization...)
                .allowCredentials(true);                   // Autorise l'envoi de cookies/credentials si besoin plus tard
    }
}
