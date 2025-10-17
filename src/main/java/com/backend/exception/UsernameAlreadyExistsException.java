package com.backend.exception;

public class UsernameAlreadyExistsException extends RuntimeException {
    public UsernameAlreadyExistsException(String username) {
        super("Nom d'utilisateur déjà existant : " + username);
    }
}
