package com.backend.service;

import com.backend.config.AppProperties;
import com.backend.dto.auth.AuthResponse;
import com.backend.dto.auth.LoginUserRequest;
import com.backend.dto.auth.RegisterUserRequest;
import com.backend.dto.user.UserResponse;
import com.backend.enums.TokenType;
import com.backend.enums.UserRole;
import com.backend.exception.*;
import com.backend.mapper.UserMapper;
import com.backend.model.TokenCheckout;
import com.backend.model.User;
import com.backend.repository.UserRepository;
import com.backend.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final TokenCheckoutService tokenCheckoutService;
    private final AppProperties appProperties;
    private final JwtTokenService tokenService;
    private final ProgressionService progressionService;

    @Transactional
    public UserResponse register(RegisterUserRequest request){
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UsernameAlreadyExistsException(request.getUsername());
        }
        String hashPwd = passwordEncoder.encode(request.getPassword());
        User user = UserMapper.toModel(request);
        user.setRole(UserRole.CLIENT);
        user.setPassword(hashPwd);
        userRepository.save(user);
        userRepository.flush();

        String tokenVerificationEmail = tokenService.generateEmailVerificationToken(user.getEmail(), 15);
        tokenCheckoutService.createToken(user, TokenType.EMAIL_VERIFICATION, 15, tokenVerificationEmail);

        String verificationLink = appProperties.getFrontend().getVerifyEmailUrl()
                + "?token=" + tokenVerificationEmail;
        Map<String, Object> variables = Map.of(
                "username", user.getUsername(),
                "verificationLink",verificationLink

        );
        String bodyHTML = mailService.buildEmailContent("notify-register.html", variables);
        String bodyText = "Bonjour " + user.getUsername() +
                ",\n\nMerci de vous être inscrit sur OpenAcademy.\n" +
                "Veuillez confirmer votre compte en cliquant sur le lien suivant :\n" +
                verificationLink +
                "\n\nSi vous n'êtes pas à l'origine de cette inscription, ignorez ce message.";

        mailService.sendMail(
                user.getEmail(),
                "Confirmez votre compte Learn Arabic",
                bodyHTML,
                bodyText
                );
        return  UserMapper.toResponse(user);
    }

    @Transactional
    public AuthResponse verifyTokenEmail(String token){
        TokenCheckout tokenCheckout = tokenCheckoutService.validateToken(token, TokenType.EMAIL_VERIFICATION);
        User user = tokenCheckout.getUser();
        if(user.isVerified()){
            throw new UserAlreadyVerifiedException();
        }
        user.setVerified(true);
        userRepository.save(user);
        tokenCheckoutService.markAsUsed(tokenCheckout);
        progressionService.initProgression(user);
        UserResponse userResponse = UserMapper.toResponse(user);
        String tokenAuth = tokenService.generateAuthToken(userResponse);
        return AuthResponse.builder().user(userResponse).token(tokenAuth).build();
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginUserRequest request){
        if (request.getEmail() == null && request.getUsername() == null) {
            throw new BadRequestException("L'adresse e-mail ou le nom d'utilisateur est obligatoire");
        }

        User user;
        if (request.getEmail() != null) {
            user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new InvalidCredentialsException("Adresse e-mail incorrecte"));
        } else {
            user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new InvalidCredentialsException("Nom d'utilisateur incorrect"));
        }
        if (!user.isVerified()) {
            throw new AccountNotVerifiedException("Veuillez vérifier votre compte avant de vous connecter");
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Mot de passe invalide");
        }
        UserResponse userResponse = UserMapper.toResponse(user);
        String token = tokenService.generateAuthToken(userResponse);

        return AuthResponse.builder()
                .user(userResponse)
                .token(token)
                .build();
    }

    @Transactional(readOnly = true)
    public AuthResponse currentUser(String currentToken){
        User currentUser = SecurityUtil.getCurrentUser();
        return AuthResponse.builder()
                .user(UserMapper.toResponse(currentUser))
                .token(currentToken)
                .build();
    }

    @Transactional
    public void resetPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("Utilisateur introuvable"));

        // Génération du token (15 minutes)
        String tokenResetPassword = tokenService.generatePasswordResetToken(email, 15);
        TokenCheckout tokenCheckout = tokenCheckoutService.createToken(
                user, TokenType.PASSWORD_RESET, 15, tokenResetPassword
        );

        // Lien vers frontend
        String resetLink = appProperties.getFrontend().getResetPasswordUrl()
                + "?token=" + tokenResetPassword;

        Map<String, Object> variables = Map.of(
                "username", user.getUsername(),
                "resetLink", resetLink
        );
        String bodyHtml = mailService.buildEmailContent("reset-password.html", variables);

        String bodyText = "Bonjour " + user.getUsername() +
                ",\n\nUne demande de réinitialisation de votre mot de passe a été faite." +
                "\nCliquez sur ce lien pour définir un nouveau mot de passe :" +
                "\n" + resetLink +
                "\n\nCe lien expirera dans 15 minutes." +
                "\nSi vous n'êtes pas à l'origine de cette demande, ignorez ce message.";

        mailService.sendMail(
                user.getEmail(),
                "Réinitialisation de votre mot de passe - OpenAcademy",
                bodyHtml,
                bodyText
        );
    }

    @Transactional
    public void confirmResetPassword(String token, String newPassword) {
        TokenCheckout tokenCheckout = tokenCheckoutService.validateToken(token, TokenType.PASSWORD_RESET);
        User user = tokenCheckout.getUser();

        // Hash du nouveau mot de passe
        String hashedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(hashedPassword);
        userRepository.save(user);

        // Marquer le token comme utilisé
        tokenCheckoutService.markAsUsed(tokenCheckout);
    }

    @Transactional
    public String resendVerification(TokenType type, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("Utilisateur introuvable"));

        if (user.isVerified()) {
            throw new UserAlreadyVerifiedException();
        }

        // ✅ Supprimer l’ancien token (optionnel mais propre)
        TokenCheckout existingToken = tokenCheckoutService.getToken(user, type);
        if (existingToken != null) {
            tokenCheckoutService.markAsUsed(existingToken);
        }

        // 🧩 Nouveau token selon le type
        if (type == TokenType.EMAIL_VERIFICATION) {
            String newToken = tokenService.generateEmailVerificationToken(email, 15);
            tokenCheckoutService.createToken(user, TokenType.EMAIL_VERIFICATION, 15, newToken);

            String verificationLink = appProperties.getFrontend().getVerifyEmailUrl()
                    + "?token=" + newToken;

            Map<String, Object> variables = Map.of(
                    "username", user.getUsername(),
                    "verificationLink", verificationLink
            );

            String bodyHTML = mailService.buildEmailContent("notify-register.html", variables);
            String bodyText = "Bonjour " + user.getUsername() +
                    ",\n\nVoici un nouveau lien pour vérifier votre compte sur Learn Arabic :\n" +
                    verificationLink +
                    "\n\nCe lien expirera dans 15 minutes.";

            mailService.sendMail(
                    user.getEmail(),
                    "Nouveau lien de vérification - Learn Arabic",
                    bodyHTML,
                    bodyText
            );

            return "Un nouveau mail de vérification a été envoyé à " + user.getEmail();
        }

        if (type == TokenType.PASSWORD_RESET) {
            String newToken = tokenService.generatePasswordResetToken(email, 15);
            tokenCheckoutService.createToken(user, TokenType.PASSWORD_RESET, 15, newToken);

            String resetLink = appProperties.getFrontend().getResetPasswordUrl()
                    + "?token=" + newToken;

            Map<String, Object> variables = Map.of(
                    "username", user.getUsername(),
                    "resetLink", resetLink
            );

            String bodyHTML = mailService.buildEmailContent("reset-password.html", variables);
            String bodyText = "Bonjour " + user.getUsername() +
                    ",\n\nVoici un nouveau lien pour réinitialiser votre mot de passe :\n" +
                    resetLink +
                    "\n\nCe lien expirera dans 15 minutes.";

            mailService.sendMail(
                    user.getEmail(),
                    "Nouveau lien de réinitialisation du mot de passe - Learn Arabic",
                    bodyHTML,
                    bodyText
            );

            return "Un nouveau mail de réinitialisation du mot de passe a été envoyé.";
        }

        throw new IllegalArgumentException("Type de token non pris en charge");
    }


}
