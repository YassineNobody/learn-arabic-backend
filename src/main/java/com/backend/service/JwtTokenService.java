package com.backend.service;

import com.backend.dto.user.UserResponse;
import com.backend.enums.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Service
public class JwtTokenService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    /**
     * Générateur générique de JWT
     */
    private String buildToken(String subject, Map<String, Object> claims, Long expirationInMinutes) {
        Date now = new Date();
        var builder = Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(now)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256);

        if (claims != null && !claims.isEmpty()) {
            builder.addClaims(claims);
        }
        if (expirationInMinutes != null) {
            builder.setExpiration(new Date(now.getTime() + expirationInMinutes * 60 * 1000));
        }

        return builder.compact();
    }

    /**
     * 🔑 Token d’auth utilisateur (pas d’expiration)
     */
    public String generateAuthToken(UserResponse user) {
        return buildToken(
                user.getEmail(),
                Map.of(
                        "id", user.getId(),
                        "uuid", user.getUuid().toString(),
                        "role", user.getRole().name(),
                        "type", "AUTH"
                ),
                null // pas d’expiration
        );
    }

    /**
     * 🔑 Token reset password (expirable)
     */
    public String generatePasswordResetToken(String email, long expirationInMinutes) {
        return buildToken(
                email,
                Map.of("type", "PASSWORD_RESET"),
                expirationInMinutes
        );
    }

    /**
     * 🔑 Token vérification email (expirable)
     */
    public String generateEmailVerificationToken(String email, long expirationInMinutes) {
        return buildToken(
                email,
                Map.of("type", "EMAIL_VERIFICATION"),
                expirationInMinutes
        );
    }

    /**
     * Extraction claims
     */
    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        return resolver.apply(extractAllClaims(token));
    }

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * ✅ Reconstruit un UserResponse à partir du token
     */
    public UserResponse extractUser(String token) {
        Claims claims = extractAllClaims(token);

        UserRole role = null;
        Object roleClaim = claims.get("role");

        if (roleClaim != null) {
            try {
                role = UserRole.valueOf(roleClaim.toString());
            } catch (IllegalArgumentException e) {
                throw new JwtException("Rôle invalide dans le token : " + roleClaim);
            }
        }

        return UserResponse.builder()
                .id(claims.get("id", Long.class))
                .uuid(UUID.fromString(claims.get("uuid", String.class)))
                .email(claims.getSubject())
                .role(role)
                .build();
    }

    /**
     * ✅ Vérifie la validité d’un token
     */
    public boolean isTokenValid(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    /**
     * ✅ Vérifie si le token correspond à un certain type
     */
    public boolean isTokenOfType(String token, String type) {
        try {
            Claims claims = extractAllClaims(token);
            return type.equals(claims.get("type", String.class));
        } catch (JwtException e) {
            return false;
        }
    }
}
