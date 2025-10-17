package com.backend.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginUserRequest {
    private String username; // peut être null

    @Email(message = "email invalide")
    private String email; // peut être null

    @NotBlank(message = "mot de passe obligatoire")
    private String password;
}