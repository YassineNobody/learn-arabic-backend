package com.backend.exception;

public class NoAuthenticatedUserException extends RuntimeException {
    public NoAuthenticatedUserException() {
        super("Aucun utilisateur authentifié trouvé.");
    }
}
