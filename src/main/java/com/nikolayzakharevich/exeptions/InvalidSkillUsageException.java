package com.nikolayzakharevich.exeptions;

public class InvalidSkillUsageException extends RuntimeException {
    public InvalidSkillUsageException() {
        super();
    }

    public InvalidSkillUsageException(String message) {
        super(message);
    }
}
