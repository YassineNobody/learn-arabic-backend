package com.backend.repository;

import com.backend.enums.TokenType;
import com.backend.model.TokenCheckout;
import com.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenCheckoutRepository extends JpaRepository<TokenCheckout, Long> {

    Optional<TokenCheckout> findByToken(String token);

    Optional<TokenCheckout> findByUserAndType(User user, TokenType type);

    void deleteByUser(User user);
}
