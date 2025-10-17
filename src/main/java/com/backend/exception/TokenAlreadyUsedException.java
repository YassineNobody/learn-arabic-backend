package com.backend.exception;

public class TokenAlreadyUsedException extends RuntimeException {
    public TokenAlreadyUsedException() {
        super("Token déjà utilisé");
    }
}
