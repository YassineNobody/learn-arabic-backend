package com.backend.controller;

import com.backend.dto.auth.AuthResponse;
import com.backend.dto.auth.LoginUserRequest;
import com.backend.dto.auth.RegisterUserRequest;
import com.backend.dto.auth.ResendVerificationRequest;
import com.backend.dto.common.SuccessResponse;
import com.backend.dto.resetpassword.ForgotPasswordRequest;
import com.backend.dto.resetpassword.ResetPasswordRequest;
import com.backend.dto.user.UserResponse;
import com.backend.exception.MissingTokenException;
import com.backend.service.AuthService;
import com.backend.util.ResponseFactory;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<SuccessResponse<UserResponse>> register(
            @RequestBody @Valid RegisterUserRequest request
    ) {
        UserResponse user = authService.register(request);
        return ResponseEntity.status(201).body(ResponseFactory.success(user));
    }

    @PostMapping("/login")
    public ResponseEntity<SuccessResponse<AuthResponse>> login(
            @RequestBody @Valid LoginUserRequest request
    ) {
        AuthResponse auth = authService.login(request);
        return ResponseEntity.ok(ResponseFactory.success(auth));
    }

    @GetMapping("/verify-email")
    public ResponseEntity<SuccessResponse<AuthResponse>> verifyEmail(
            @RequestParam("token") String token
    ) {
        AuthResponse auth = authService.verifyTokenEmail(token);
        return ResponseEntity.ok(ResponseFactory.success(auth));
    }

    @GetMapping("/me")
    public ResponseEntity<SuccessResponse<AuthResponse>> currentUser(
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new MissingTokenException("Token manquant ou invalide");
        }
        String token = authorization.substring(7);
        AuthResponse auth = authService.currentUser(token);
        return ResponseEntity.ok(ResponseFactory.success(auth));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<SuccessResponse<String>>forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request
    ){
        authService.resetPassword(request.getEmail());
        return ResponseEntity.ok(ResponseFactory.success("Email envoyé"));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<SuccessResponse<String>>resetPassword(
            @Valid @RequestBody ResetPasswordRequest request
    ){
        authService.confirmResetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok(ResponseFactory.success("Mot de passe réinitialisé"));
    }


    @PostMapping("/resend-verification")
    public ResponseEntity<SuccessResponse<String>> resendVerification(
            @Valid @RequestBody ResendVerificationRequest request
    ) {
        String message = authService.resendVerification(request.getType(), request.getEmail());
        return ResponseEntity.ok(ResponseFactory.success(message));
    }
}

