package com.university.exception.students;

public class AlreadySubmittedException extends RuntimeException {
    public AlreadySubmittedException(String message) {
        super(message);
    }
}