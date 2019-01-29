package com.nikolayzakharevich.exeptions;

public class InvalidPayloadException extends RuntimeException {

    public InvalidPayloadException() {
        super();
    }

    public InvalidPayloadException(String message) {
        super(message);
    }
}
