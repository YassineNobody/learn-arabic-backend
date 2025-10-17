package com.backend.exception;


public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String email) {
        super("Adresse email déjà existante : " + email);
    }
}

