package com.boutique.user.exception;

public class UserStateConflictException extends RuntimeException {

    public UserStateConflictException(String message) {
        super(message);
    }
}
