package com.backend.service;

import com.backend.enums.TokenType;
import com.backend.exception.TokenAlreadyUsedException;
import com.backend.exception.TokenExpiredException;
import com.backend.exception.TokenInvalidException;
import com.backend.model.TokenCheckout;
import com.backend.model.User;
import com.backend.repository.TokenCheckoutRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TokenCheckoutService {
    private final TokenCheckoutRepository tokenCheckoutRepository;

    @Transactional
    public TokenCheckout createToken(User user, TokenType type, long expirationMinutes, String rawToken) {
        TokenCheckout token = TokenCheckout.builder()
                .token(rawToken)
                .type(type)
                .user(user)
                .expiresAt(LocalDateTime.now().plusMinutes(expirationMinutes))
                .used(false)
                .build();
        return tokenCheckoutRepository.save(token);
    }

    @Transactional(readOnly = true)
    public TokenCheckout validateToken(String token, TokenType type) {
        TokenCheckout tokenEntity = tokenCheckoutRepository.findByToken(token)
                .orElseThrow(TokenInvalidException::new);

        if (tokenEntity.isUsed()) {
            throw new TokenAlreadyUsedException();
        }

        if (tokenEntity.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException();
        }

        if (tokenEntity.getType() != type) {
            throw new TokenInvalidException();
        }

        return tokenEntity;
    }

    @Transactional
    public void markAsUsed(TokenCheckout token) {
        token.setUsed(true);
        tokenCheckoutRepository.save(token);
    }

    @Transactional(readOnly = true)
    public TokenCheckout getToken(User user, TokenType type){
        var token = tokenCheckoutRepository.findByUserAndType(user, type);
        return token.orElse(null);
    }
}