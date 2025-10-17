package com.backend.dto.auth;

import com.backend.enums.TokenType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResendVerificationRequest {
    @Email
    @NotNull
    private String email;

    @NotNull
    private TokenType type;
}
