package com.backend.exception;

import com.backend.dto.common.ErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSendException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(
                        ex.getMessage(),
                        "NOT_FOUND",
                        "La ressource demandée est introuvable."
                ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(
                        ex.getMessage(),
                        "BAD_REQUEST",
                        "Requête invalide."
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        StringBuilder description = new StringBuilder();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            description.append("[").append(fieldError.getField())
                    .append("] : ").append(fieldError.getDefaultMessage()).append(" | ");
        }
        if (description.length() > 3) {
            description.setLength(description.length() - 3);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(
                        "Erreur de validation",
                        "VALIDATION_ERROR",
                        description.toString()
                ));
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ErrorResponse> handleMissingHeader(MissingRequestHeaderException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(
                        "Accès non autorisé",
                        "UNAUTHORIZED",
                        "Le header 'Authorization' est requis."
                ));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(
                        "Accès refusé",
                        "FORBIDDEN",
                        "Vous n'avez pas les droits nécessaires pour effectuer cette action."
                ));
    }

    @ExceptionHandler(MailSendException.class)
    public ResponseEntity<ErrorResponse> handleMailError(MailSendException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(
                        "Erreur lors de l'envoi d'email",
                        "MAIL_ERROR",
                        ex.getMessage()
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(
                        "Erreur interne du serveur",
                        "INTERNAL_ERROR",
                        ex.getMessage()
                ));
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEmailExists(EmailAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(
                        ex.getMessage(),
                        "EMAIL_ALREADY_EXISTS",
                        "Un compte existe déjà avec cette adresse email."
                ));
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUsernameExists(UsernameAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(
                        ex.getMessage(),
                        "USERNAME_ALREADY_EXISTS",
                        "Un compte existe déjà avec ce nom d'utilisateur."
                ));
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ErrorResponse> handleTokenExpired(TokenExpiredException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(
                        ex.getMessage(),
                        "TOKEN_EXPIRED",
                        "Veuillez redemander un nouveau lien de vérification."
                ));
    }

    @ExceptionHandler({TokenInvalidException.class, TokenAlreadyUsedException.class})
    public ResponseEntity<ErrorResponse> handleTokenInvalid(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(
                        ex.getMessage(),
                        "TOKEN_INVALID",
                        "Le token fourni n'est pas valide."
                ));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(InvalidCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(
                        ex.getMessage(),
                        "INVALID_CREDENTIALS",
                        "Les identifiants fournis sont incorrects."
                ));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(BadRequestException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(
                        ex.getMessage(),
                        "BAD_REQUEST",
                        "Requête invalide."
                ));
    }


    @ExceptionHandler(MissingTokenException.class)
    public ResponseEntity<ErrorResponse> handleMissingToken(MissingTokenException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(
                        ex.getMessage(),
                        "MISSING_TOKEN",
                        "Le header 'Authorization' avec un Bearer token est requis."
                ));
    }


    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleEnumBindingErrors(MethodArgumentTypeMismatchException ex) {
        String paramName = ex.getName();
        String invalidValue = ex.getValue() != null ? ex.getValue().toString() : "null";

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(
                        String.format("La valeur '%s' n'est pas valide pour le paramètre '%s'.", invalidValue, paramName),
                        "INVALID_ENUM",
                        "La valeur fournie ne correspond à aucun élément attendu (enum)."
                ));
    }

    @ExceptionHandler(NoAuthenticatedUserException.class)
    public ResponseEntity<ErrorResponse> handleNoAuthenticatedUser(NoAuthenticatedUserException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(
                        ex.getMessage(),
                        "NO_AUTH_USER",
                        "L'utilisateur n'est pas authentifié."
                ));
    }

    @ExceptionHandler(AccountNotVerifiedException.class)
    public ResponseEntity<ErrorResponse> handleAccountNotVerified(AccountNotVerifiedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(
                        ex.getMessage(),
                        "ACCOUNT_NOT_VERIFIED",
                        "Veuillez vérifier votre compte avant de vous connecter."
                ));
    }
    @ExceptionHandler(UserAlreadyVerifiedException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyVerified(UserAlreadyVerifiedException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(
                        ex.getMessage(),
                        "USER_ALREADY_VERIFIED",
                        "L'utilisateur a déjà confirmé son compte."
                ));
    }
    @ExceptionHandler(CategoryAlreadyExist.class)
    public ResponseEntity<ErrorResponse> handleCategoryAlreadyExiste(CategoryAlreadyExist ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(
                        ex.getMessage(),
                        "CATEGORY_ALREADY_EXISTE",
                        "La catégorie existe déjà"
                ));
    }
    @ExceptionHandler(ResourceConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflict(ResourceConflictException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(
                        ex.getMessage(),
                        "DOCUMENT_ALREADY_EXISTS",
                        "Le document existe déjà"
                ));
    }

}
