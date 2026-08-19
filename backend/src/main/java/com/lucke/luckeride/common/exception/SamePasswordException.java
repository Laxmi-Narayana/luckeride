package com.lucke.luckeride.common.exception;

public class SamePasswordException
        extends RuntimeException {

    public SamePasswordException() {
        super("New password must be different from current password");
    }
}