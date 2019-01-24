package com.nikolayzakharevich.exeptions;

public class KeyboardException extends RuntimeException {
    public KeyboardException() {
    }

    public KeyboardException(String message) {
        super(message);
    }
}
