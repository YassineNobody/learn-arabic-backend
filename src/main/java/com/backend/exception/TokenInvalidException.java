package com.backend.exception;

public class TokenInvalidException extends RuntimeException {
    public TokenInvalidException() {
        super("Token invalide");
    }
}
