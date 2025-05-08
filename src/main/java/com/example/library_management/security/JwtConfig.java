package com.example.library_management.security;

import io.jsonwebtoken.security.Keys;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.Key;

@Configuration
public class JwtConfig {
    private static final String SECRET = "AutoServiceSecretKeyAutoServiceSecretKeyAutoServiceSecretKey";

    @Bean
    public Key jwtSecretоKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }
}
