package com.backend.filter;


import com.backend.dto.common.ErrorResponse;
import com.backend.model.User;
import com.backend.repository.UserRepository;
import com.backend.service.JwtTokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenService jwtTokenService;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    public JwtAuthenticationFilter(JwtTokenService jwtTokenService, UserRepository userRepository, ObjectMapper objectMapper
    ) {
        this.jwtTokenService = jwtTokenService;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }

    private String extractTokenFromHeader(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7); // Enlève "Bearer "
        }
        return null;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String token = extractTokenFromHeader(request);
        if (token != null) {
            try {
                String email = jwtTokenService.extractEmail(token);
                Optional<User> userOpt = userRepository.findByEmail(email);

                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (ExpiredJwtException e) {
                writeUnauthorizedResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "TOKEN_EXPIRED", "Le token JWT est expiré.");
                return;
            } catch (Exception e) {
                writeUnauthorizedResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "TOKEN_INVALID", "Échec de la validation du token.");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private void writeUnauthorizedResponse(HttpServletResponse response, int status, String errorCode, String description) throws IOException {
        ErrorResponse errorResponse = new ErrorResponse(
                "Accès refusé",
                errorCode,
                description
        );
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }
}

