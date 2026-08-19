package com.lucke.luckeride.common.exception;

public class InvalidCurrentPasswordException
        extends RuntimeException {

    public InvalidCurrentPasswordException() {
        super("Current password is incorrect");
    }
}