package com.backend.dto.auth;


import com.backend.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterUserRequest {



    @NotBlank(message = "email obligatoire")
    @Email(message = "email invalide")
    private String email;

    @NotBlank(message = "mot de passe obligatoire")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*()\\-_=+{}\\[\\]:;\"'<>,.?/~`|\\\\]).{8,}$",
            message = "Le mot de passe doit contenir au moins 8 caractères, une majuscule, un chiffre et un caractère spécial"
    )
    private String password;


    @NotBlank(message = "nom d'utilisateur obligatoire")
    private String username;


}
