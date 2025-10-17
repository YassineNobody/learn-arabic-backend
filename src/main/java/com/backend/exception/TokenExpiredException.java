package com.backend.exception;

public class TokenExpiredException extends RuntimeException {
    public TokenExpiredException() {
        super("Token expiré");
    }
}
