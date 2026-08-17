package com.lucke.luckeride.auth.exception;

public class InvalidRefreshTokenException extends RuntimeException {

    public InvalidRefreshTokenException(String reason) {
        super(reason);
    }
}